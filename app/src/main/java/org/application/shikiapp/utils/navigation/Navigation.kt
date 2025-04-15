package org.application.shikiapp.utils.navigation

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
import org.application.shikiapp.utils.BASE_PATH
import org.application.shikiapp.utils.REDIRECT_URI
import org.application.shikiapp.utils.enums.Menu
import org.application.shikiapp.utils.extensions.NavigationBarVisibility
import org.application.shikiapp.utils.isCurrentRoute
import org.application.shikiapp.utils.navigation.Screen.Anime
import org.application.shikiapp.utils.navigation.Screen.AnimeRates
import org.application.shikiapp.utils.navigation.Screen.Calendar
import org.application.shikiapp.utils.navigation.Screen.Catalog
import org.application.shikiapp.utils.navigation.Screen.Club
import org.application.shikiapp.utils.navigation.Screen.Login
import org.application.shikiapp.utils.navigation.Screen.Manga
import org.application.shikiapp.utils.navigation.Screen.MangaRates
import org.application.shikiapp.utils.navigation.Screen.News
import org.application.shikiapp.utils.navigation.Screen.NewsDetail
import org.application.shikiapp.utils.navigation.Screen.Person
import org.application.shikiapp.utils.navigation.Screen.Profile
import org.application.shikiapp.utils.navigation.Screen.Settings
import org.application.shikiapp.utils.navigation.Screen.User

@Composable
fun Navigation(navigator: NavHostController, visibility: NavigationBarVisibility, modifier: Modifier) {
    NavHost(navigator, News, modifier.consumeWindowInsets(WindowInsets.systemBars)) {
        // Bottom menu items //
        composable<Catalog> {
            CatalogScreen(visibility, navigator::navigate)
        }
        composable<News> {
            NewsScreen(navigator::navigate)
        }
        composable<Calendar> {
            CalendarScreen(navigator::navigate)
        }
        composable<Profile>(
            deepLinks = listOf(
                navDeepLink<Login>(BASE_PATH) {
                    uriPattern = "$REDIRECT_URI?code={code}"
                }
            )
        ) {
            ProfileScreen(navigator::navigate, visibility)
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
            AnimeScreen(navigator::navigate, navigator::navigateUp)
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
            MangaScreen(navigator::navigate, navigator::navigateUp)
        }
        composable<Screen.Character>(
            deepLinks = listOf(
                navDeepLink<Character>(BASE_PATH) {
                    action = Intent.ACTION_VIEW
                    uriPattern = "https://shikimori.one/characters/{id}-.*"
                }
            )
        ) {
            CharacterScreen(navigator::navigate, navigator::navigateUp)
        }
        composable<Person>(
            deepLinks = listOf(
                navDeepLink<Person>(BASE_PATH) {
                    action = Intent.ACTION_VIEW
                    uriPattern = "https://shikimori.one/people/{id}-.*"
                }
            )
        ) {
            PersonScreen(navigator::navigate, navigator::navigateUp)
        }
        composable<User> {
            UserScreen(navigator::navigate, navigator::navigateUp)
        }
        composable<Club> {
            ClubScreen(navigator::navigate, navigator::navigateUp)
        }
        composable<NewsDetail> {
            NewsDetail(navigator::navigate, navigator::navigateUp)
        }
        composable<AnimeRates> {
            AnimeRatesScreen(navigator::navigate, navigator::navigateUp)
        }
        composable<MangaRates> {
            MangaRatesScreen(navigator::navigate, navigator::navigateUp)
        }
    }
}

@Composable
fun BottomNavigationBar(backStack: NavBackStackEntry?, visible: Boolean, onClick: (Screen) -> Unit) {
    val initial = MaterialTheme.typography.labelMedium

    var style by remember { mutableStateOf(initial) }
    var draw by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = Menu.entries.any { backStack.isCurrentRoute(it.route::class) } && visible,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        NavigationBar {
            Menu.entries.forEach { screen ->
                NavigationBarItem(
                    selected = backStack.isCurrentRoute(screen.route::class),
                    alwaysShowLabel = false,
                    icon = { Icon(painterResource(screen.icon), null) },
                    onClick = { onClick(screen.route) },
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