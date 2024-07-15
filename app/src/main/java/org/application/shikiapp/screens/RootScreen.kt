package org.application.shikiapp.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.CalendarScreenDestination
import com.ramcosta.composedestinations.generated.destinations.CatalogScreenDestination
import com.ramcosta.composedestinations.generated.destinations.NewsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ProfileScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.isRouteOnBackStack
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator
import org.application.shikiapp.R.string.text_calendar
import org.application.shikiapp.R.string.text_catalog
import org.application.shikiapp.R.string.text_news
import org.application.shikiapp.R.string.text_profile
import org.application.shikiapp.R.string.text_settings

@Composable
fun RootScreen(navController: NavHostController) {
    val navigator = navController.rememberDestinationsNavigator()
    val current by navController.currentDestinationAsState()

    Scaffold(
        bottomBar = {
            if (Menu.entries.any { it.route == current }) NavigationBar {
                Menu.entries.forEach { screen ->
                    NavigationBarItem(
                        selected = current == screen.route,
                        onClick = {
                            if (navController.isRouteOnBackStack(screen.route)) {
                                navigator.popBackStack(screen.route, false)
                                return@NavigationBarItem
                            }

                            navigator.navigate(screen.route) {
                                popUpTo(NavGraphs.root) { saveState = true }

                                launchSingleTop = true
                                restoreState = true
                            }

                        },
                        icon = { Icon(screen.icon, null) },
                        label = {
                            Text(
                                text = stringResource(screen.title),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        }
    ) { paddingValues ->
        DestinationsNavHost(
            navGraph = NavGraphs.root,
            modifier = Modifier.padding(paddingValues),
            startRoute = NewsScreenDestination,
            navController = navController
        )
    }
}

enum class Menu(val route: DirectionDestinationSpec, val title: Int, val icon: ImageVector) {
    Anime(CatalogScreenDestination, text_catalog, Icons.Default.Home),
    News(NewsScreenDestination, text_news, Icons.Default.Info),
    Calendar(CalendarScreenDestination, text_calendar, Icons.Default.DateRange),
    Profile(ProfileScreenDestination, text_profile, Icons.Default.Face),
    Settings(SettingsScreenDestination, text_settings, Icons.Default.Settings)
}