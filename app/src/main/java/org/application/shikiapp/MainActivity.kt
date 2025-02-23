package org.application.shikiapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import org.application.shikiapp.screens.AnimeRatesScreen
import org.application.shikiapp.screens.AnimeScreen
import org.application.shikiapp.screens.CalendarScreen
import org.application.shikiapp.screens.CatalogScreen
import org.application.shikiapp.screens.CharacterScreen
import org.application.shikiapp.screens.ClubScreen
import org.application.shikiapp.screens.MangaRatesScreen
import org.application.shikiapp.screens.MangaScreen
import org.application.shikiapp.screens.NewsDetail
import org.application.shikiapp.screens.NewsScreen
import org.application.shikiapp.screens.PersonScreen
import org.application.shikiapp.screens.ProfileScreen
import org.application.shikiapp.screens.SettingsScreen
import org.application.shikiapp.screens.UserScreen
import org.application.shikiapp.ui.theme.Theme
import org.application.shikiapp.utils.Anime
import org.application.shikiapp.utils.AnimeRates
import org.application.shikiapp.utils.BASE_PATH
import org.application.shikiapp.utils.Calendar
import org.application.shikiapp.utils.Catalog
import org.application.shikiapp.utils.Character
import org.application.shikiapp.utils.Club
import org.application.shikiapp.utils.Login
import org.application.shikiapp.utils.Manga
import org.application.shikiapp.utils.MangaRates
import org.application.shikiapp.utils.Menu
import org.application.shikiapp.utils.News
import org.application.shikiapp.utils.NewsDetail
import org.application.shikiapp.utils.Person
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.Profile
import org.application.shikiapp.utils.REDIRECT_URI
import org.application.shikiapp.utils.Settings
import org.application.shikiapp.utils.User
import org.application.shikiapp.utils.isCurrentRoute
import org.application.shikiapp.utils.toBottomBarItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actionBar?.hide()
        enableEdgeToEdge()
        setContent {
            val navigator = rememberNavController()
            val backStack by navigator.currentBackStackEntryAsState()

            val theme by Preferences.theme.collectAsStateWithLifecycle()
            val dynamicColors by Preferences.dynamicColors.collectAsStateWithLifecycle()

            val initial = MaterialTheme.typography.labelMedium

            var style by remember { mutableStateOf(initial) }
            var draw by remember { mutableStateOf(false) }

            Theme(theme, dynamicColors) {
                Scaffold(
                    modifier = Modifier.safeDrawingPadding(),
                    bottomBar = {
                        AnimatedVisibility(
                            visible = Menu.entries.any { backStack.isCurrentRoute(it.route::class) },
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            NavigationBar {
                                Menu.entries.forEach { screen ->
                                    NavigationBarItem(
                                        selected = backStack.isCurrentRoute(screen.route::class),
                                        alwaysShowLabel = false,
                                        icon = { Icon(painterResource(screen.icon), null) },
                                        onClick = { navigator.toBottomBarItem(screen.route) },
                                        label = {
                                            Text(
                                                text = stringResource(screen.title),
                                                softWrap = false,
                                                modifier = Modifier.drawWithContent { if (draw) drawContent() },
                                                style = style,
                                                onTextLayout = {
                                                    if (!it.didOverflowWidth) draw = true
                                                    else style = style.copy(fontSize = style.fontSize * 0.95)
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { values ->
                    NavHost(navigator, News, Modifier.padding(values)) {
                        // Bottom menu items //
                        composable<Catalog> {
                            CatalogScreen(
                                navigateToAnime = { navigator.navigate(Anime(it)) },
                                navigateToManga = { navigator.navigate(Manga(it)) },
                                navigateToCharacter = { navigator.navigate(Character(it)) },
                                navigateToPerson = { navigator.navigate(Person(it)) },
                            )
                        }
                        composable<News> {
                            NewsScreen { navigator.navigate(NewsDetail(it)) }
                        }
                        composable<Calendar> {
                            CalendarScreen { navigator.navigate(Anime(it)) }
                        }
                        composable<Profile>(
                            deepLinks = listOf(
                                navDeepLink<Login>(BASE_PATH) {
                                    uriPattern = "$REDIRECT_URI?code={code}"
                                }
                            )
                        ) {
                            ProfileScreen(
                                toAnime = { navigator.navigate(AnimeRates(it.toLong())) },
                                toManga = { navigator.navigate(MangaRates(it.toLong())) },
                                toCharacter = { navigator.navigate(Character(it)) },
                                toPerson = { navigator.navigate(Person(it)) },
                                toUser = { navigator.navigate(User(it)) },
                                toClub = { navigator.navigate(Club(it)) }
                            )
                        }
                        composable<Settings> {
                            SettingsScreen()
                        }

                        // Screens //
                        composable<Anime>(
                            deepLinks = listOf(
                                navDeepLink<Anime>(BASE_PATH) {
                                    action = Intent.ACTION_VIEW
                                    uriPattern = "https://shikimori.one/animes/{id}-.*"
                                }
                            )
                        ) {
                            AnimeScreen(
                                toAnime = { navigator.navigate(Anime(it)) },
                                toManga = { navigator.navigate(Manga(it)) },
                                toCharacter = { navigator.navigate(Character(it)) },
                                toPerson = { navigator.navigate(Person(it)) },
                                toUser = { navigator.navigate(User(it)) },
                                back = navigator::navigateUp
                            )
                        }
                        composable<Manga>(
                            deepLinks = listOf(
                                navDeepLink<Manga>(BASE_PATH) {
                                    action = Intent.ACTION_VIEW
                                    uriPattern = "https://shikimori.one/mangas/{id}-.*"
                                },
                                navDeepLink<Manga>(BASE_PATH) {
                                    action = Intent.ACTION_VIEW
                                    uriPattern = "https://shikimori.one/ranobe/{id}-.*"
                                }
                            )
                        ) {
                            MangaScreen(
                                toAnime = { navigator.navigate(Anime(it)) },
                                toManga = { navigator.navigate(Manga(it)) },
                                toCharacter = { navigator.navigate(Character(it)) },
                                toPerson = { navigator.navigate(Person(it)) },
                                toUser = { navigator.navigate(User(it)) },
                                back = navigator::navigateUp
                            )
                        }
                        composable<Character>(
                            deepLinks = listOf(
                                navDeepLink<Character>(BASE_PATH) {
                                    action = Intent.ACTION_VIEW
                                    uriPattern = "https://shikimori.one/characters/{id}-.*"
                                }
                            )
                        ) {
                            CharacterScreen(
                                toAnime = { navigator.navigate(Anime(it)) },
                                toManga = { navigator.navigate(Manga(it)) },
                                toPerson = { navigator.navigate(Person(it)) },
                                toUser = { navigator.navigate(User(it)) },
                                back = navigator::navigateUp
                            )
                        }
                        composable<Person>(
                            deepLinks = listOf(
                                navDeepLink<Person>(BASE_PATH) {
                                    action = Intent.ACTION_VIEW
                                    uriPattern = "https://shikimori.one/people/{id}-.*"
                                }
                            )
                        ) {
                            PersonScreen(
                                toCharacter = { navigator.navigate(Character(it)) },
                                toUser = { navigator.navigate(User(it)) },
                                back = navigator::navigateUp
                            )
                        }
                        composable<User> {
                            UserScreen(
                                toAnime = { navigator.navigate(AnimeRates(it.toLong())) },
                                toManga = { navigator.navigate(MangaRates(it.toLong())) },
                                toCharacter = { navigator.navigate(Character(it)) },
                                toPerson = { navigator.navigate(Person(it)) },
                                toUser = { navigator.navigate(User(it)) },
                                toClub = { navigator.navigate(Club(it)) },
                                back = navigator::navigateUp
                            )
                        }
                        composable<Club> {
                            ClubScreen(
                                toAnime = { navigator.navigate(Anime(it)) },
                                toCharacter = { navigator.navigate(Character(it)) },
                                toUser = { navigator.navigate(User(it)) },
                                back = navigator::navigateUp
                            )
                        }
                        composable<NewsDetail> {
                            NewsDetail(
                                toUser = { navigator.navigate(User(it)) },
                                back = navigator::navigateUp
                            )
                        }
                        composable<AnimeRates> {
                            AnimeRatesScreen(
                                toAnime = { navigator.navigate(Anime(it)) },
                                back = navigator::navigateUp
                            )
                        }
                        composable<MangaRates> {
                            MangaRatesScreen(
                                toManga = { navigator.navigate(Manga(it)) },
                                back = navigator::navigateUp
                            )
                        }
                    }
                }
            }
        }
    }
}