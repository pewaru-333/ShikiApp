package org.application.shikiapp.shared.utils.navigation

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navDeepLink
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.screens.AnimeScreen
import org.application.shikiapp.shared.screens.CalendarScreen
import org.application.shikiapp.shared.screens.CatalogScreen
import org.application.shikiapp.shared.screens.CharacterScreen
import org.application.shikiapp.shared.screens.ClubScreen
import org.application.shikiapp.shared.screens.MangaScreen
import org.application.shikiapp.shared.screens.NewsDetail
import org.application.shikiapp.shared.screens.NewsScreen
import org.application.shikiapp.shared.screens.PersonScreen
import org.application.shikiapp.shared.screens.ProfileScreen
import org.application.shikiapp.shared.screens.UserRates
import org.application.shikiapp.shared.screens.UserScreen
import org.application.shikiapp.shared.ui.templates.VectorIcon
import org.application.shikiapp.shared.utils.BASE_PATH
import org.application.shikiapp.shared.utils.BASE_URL
import org.application.shikiapp.shared.utils.REDIRECT_URI
import org.application.shikiapp.shared.utils.URL_MIRROR
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.enums.Menu
import org.application.shikiapp.shared.utils.extensions.isCurrentRoute
import org.application.shikiapp.shared.utils.extensions.toBottomBarItem
import org.application.shikiapp.shared.utils.navigation.Screen.Anime
import org.application.shikiapp.shared.utils.navigation.Screen.Calendar
import org.application.shikiapp.shared.utils.navigation.Screen.Club
import org.application.shikiapp.shared.utils.navigation.Screen.Login
import org.application.shikiapp.shared.utils.navigation.Screen.Manga
import org.application.shikiapp.shared.utils.navigation.Screen.News
import org.application.shikiapp.shared.utils.navigation.Screen.NewsDetail
import org.application.shikiapp.shared.utils.navigation.Screen.Person
import org.application.shikiapp.shared.utils.navigation.Screen.Profile
import org.application.shikiapp.shared.utils.navigation.Screen.User
import org.application.shikiapp.shared.utils.serializableNavType
import org.jetbrains.compose.resources.stringResource
import kotlin.reflect.typeOf

@Composable
fun Navigation(navigator: NavHostController) {
    val backStack by navigator.currentBackStackEntryAsState()
    val barVisibility = LocalBarVisibility.current
    val adaptiveInfo = currentWindowAdaptiveInfo()

    val routes = remember { Menu.entries.map { it.route::class } }
    val isTopLevel = remember(backStack) {
        routes.any { backStack.isCurrentRoute(it) }
    }

    val suiteType = if (isTopLevel && barVisibility.isVisible) {
        NavigationSuiteScaffoldDefaults.navigationSuiteType(adaptiveInfo)
    } else {
        NavigationSuiteType.None
    }

    fun getLabel(screen: Menu): (@Composable () -> Unit)? {
        val isSelected = backStack.isCurrentRoute(screen.route::class)

        if (suiteType == NavigationSuiteType.ShortNavigationBarCompact && !isSelected) {
            return null
        }

        return {
            if (suiteType == NavigationSuiteType.ShortNavigationBarCompact) {
                Text(
                    text = stringResource(screen.title),
                    softWrap = false,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 1.sp,
                        maxFontSize = LocalTextStyle.current.fontSize,
                        stepSize = 0.5.sp
                    )
                )
            } else {
                Text(
                    text = stringResource(screen.title),
                    softWrap = false,
                    maxLines = 1
                )
            }
        }
    }

    NavigationSuiteScaffold(
        navigationSuiteType = suiteType,
        content = { AppNavHost(navigator) },
        navigationItems = {
            Menu.entries.forEach { screen ->
                NavigationSuiteItem(
                    selected = backStack.isCurrentRoute(screen.route::class),
                    onClick = { navigator.toBottomBarItem(screen.route) },
                    icon = { VectorIcon(screen.icon) },
                    label = getLabel(screen)
                )
            }
        }
    )
}

@Composable
private fun AppNavHost(navigator: NavHostController) =
    NavHost(navigator, Preferences.startPage.route, Modifier.systemBarsPadding()) {
        // Bottom menu items //
        composable<Screen.Catalog>(
            typeMap = mapOf(
                typeOf<Boolean?>() to serializableNavType(Boolean.serializer().nullable),
                typeOf<LinkedType?>() to serializableNavType(LinkedType.serializer().nullable)
            )
        ) {
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
                    uriPattern = "$BASE_URL/animes/{id}-.*"
                },
                navDeepLink<Anime>(BASE_PATH) {
                    uriPattern = "$URL_MIRROR/animes/{id}-.*"
                }
            )
        ) {
            AnimeScreen(navigator::navigate, navigator::navigateUp)
        }
        composable<Manga>(
            deepLinks = listOf(
                navDeepLink<Manga>(BASE_PATH) {
                    uriPattern = "$BASE_URL/mangas/{id}-.*"
                },
                navDeepLink<Manga>(BASE_PATH) {
                    uriPattern = "$BASE_URL/ranobe/{id}-.*"
                },
                navDeepLink<Manga>(BASE_PATH) {
                    uriPattern = "$URL_MIRROR/mangas/{id}-.*"
                },
                navDeepLink<Manga>(BASE_PATH) {
                    uriPattern = "$URL_MIRROR/ranobe/{id}-.*"
                }
            )
        ) {
            MangaScreen(navigator::navigate, navigator::navigateUp)
        }
        composable<Screen.Character>(
            deepLinks = listOf(
                navDeepLink<Screen.Character>(BASE_PATH) {
                    uriPattern = "$BASE_URL/characters/{id}-.*"
                },
                navDeepLink<Screen.Character>(BASE_PATH) {
                    uriPattern = "$URL_MIRROR/characters/{id}-.*"
                }
            )
        ) {
            CharacterScreen(navigator::navigate, navigator::navigateUp)
        }
        composable<Person>(
            deepLinks = listOf(
                navDeepLink<Person>(BASE_PATH) {
                    uriPattern = "$BASE_URL/people/{id}-.*"
                },
                navDeepLink<Person>(BASE_PATH) {
                    uriPattern = "$URL_MIRROR/people/{id}-.*"
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
        composable<Screen.UserRates>(
            typeMap = mapOf(
                typeOf<LinkedType?>() to serializableNavType(LinkedType.serializer().nullable)
            )
        ) {
            UserRates(navigator::navigate, navigator::navigateUp)
        }
    }