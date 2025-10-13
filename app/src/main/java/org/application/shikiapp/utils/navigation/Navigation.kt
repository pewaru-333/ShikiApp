package org.application.shikiapp.utils.navigation

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navDeepLink
import org.application.shikiapp.screens.AnimeScreen
import org.application.shikiapp.screens.CalendarScreen
import org.application.shikiapp.screens.CatalogScreen
import org.application.shikiapp.screens.CharacterScreen
import org.application.shikiapp.screens.ClubScreen
import org.application.shikiapp.screens.MangaScreen
import org.application.shikiapp.screens.NewsDetail
import org.application.shikiapp.screens.NewsScreen
import org.application.shikiapp.screens.PersonScreen
import org.application.shikiapp.screens.ProfileScreen
import org.application.shikiapp.screens.UserRates
import org.application.shikiapp.screens.UserScreen
import org.application.shikiapp.utils.BASE_PATH
import org.application.shikiapp.utils.REDIRECT_URI
import org.application.shikiapp.utils.enums.Menu
import org.application.shikiapp.utils.extensions.isCurrentRoute
import org.application.shikiapp.utils.extensions.toBottomBarItem
import org.application.shikiapp.utils.navigation.Screen.Anime
import org.application.shikiapp.utils.navigation.Screen.Calendar
import org.application.shikiapp.utils.navigation.Screen.Catalog
import org.application.shikiapp.utils.navigation.Screen.Club
import org.application.shikiapp.utils.navigation.Screen.Login
import org.application.shikiapp.utils.navigation.Screen.Manga
import org.application.shikiapp.utils.navigation.Screen.News
import org.application.shikiapp.utils.navigation.Screen.NewsDetail
import org.application.shikiapp.utils.navigation.Screen.Person
import org.application.shikiapp.utils.navigation.Screen.Profile
import org.application.shikiapp.utils.navigation.Screen.User
import kotlin.reflect.KClass

@Composable
fun Navigation(navigator: NavHostController) {
    val barVisibility = LocalBarVisibility.current
    val backStack by navigator.currentBackStackEntryAsState()

    val routes = remember { Menu.entries.map { it.route::class } }

    LaunchedEffect(backStack, barVisibility) {
        if (routes.any { backStack.isCurrentRoute(it) }) barVisibility.show()
        else barVisibility.hide()
    }

    Scaffold(
        content = { AppNavHost(navigator, Modifier.padding(it)) },
        bottomBar = {
            AnimatedVisibility(
                visible = barVisibility.isVisible,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                BottomNavigationBar(
                    selected = { backStack.isCurrentRoute(it) },
                    onClick = { navigator.toBottomBarItem(it) }
                )
            }
        }
    )
}

@Composable
private fun AppNavHost(navigator: NavHostController, modifier: Modifier) =
    NavHost(navigator, News, modifier.consumeWindowInsets(WindowInsets.systemBars)) {
        // Bottom menu items //
        composable<Catalog> {
            CatalogScreen(navigator::navigate)
        }
        composable<Calendar> {
            CalendarScreen(navigator::navigate)
        }
        composable<News> {
            NewsScreen(navigator::navigate)
        }
        composable<Profile>(
            deepLinks = listOf(
                navDeepLink<Login>(BASE_PATH) {
                    uriPattern = "$REDIRECT_URI?code={code}"
                }
            )
        ) {
            ProfileScreen(navigator::navigate)
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
        composable<Screen.UserRates> {
            UserRates(navigator::navigate, navigator::navigateUp)
        }
    }

@Composable
private fun BottomNavigationBar(selected: (route: KClass<*>) -> Boolean, onClick: (Screen) -> Unit) =
        NavigationBar {
            Menu.entries.forEach { screen ->
                NavigationBarItem(
                    selected = selected(screen.route::class),
                    alwaysShowLabel = false,
                    icon = { Icon(painterResource(screen.icon), null) },
                    onClick = { onClick(screen.route) },
                    label = {
                        Text(
                            text = stringResource(screen.title),
                            softWrap = false,
//                            style = LocalTextStyle.current.copy(
//                                color = LocalContentColor.current
//                            ),
                            autoSize = TextAutoSize.StepBased(
                                minFontSize = 1.sp,
                                maxFontSize = LocalTextStyle.current.fontSize,
                                stepSize = (0.1).sp
                            )
                        )
                    }
                )
            }
        }