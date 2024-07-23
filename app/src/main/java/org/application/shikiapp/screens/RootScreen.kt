package org.application.shikiapp.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.DefaultFadingTransitions
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.NewsScreenDestination
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.isRouteOnBackStack
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator
import org.application.shikiapp.utils.BottomMenu

@Composable
fun RootScreen(navController: NavHostController) {
    val navigator = navController.rememberDestinationsNavigator()
    val current by navController.currentDestinationAsState()

    Scaffold(
        bottomBar = {
            if (BottomMenu.entries.any { it.route == current }) NavigationBar {
                BottomMenu.entries.forEach { screen ->
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
                        icon = { Icon(painterResource(screen.icon), null) },
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
    ) { padding ->
        DestinationsNavHost(
            navGraph = NavGraphs.root,
            modifier = Modifier.padding(padding),
            startRoute = NewsScreenDestination,
            defaultTransitions = DefaultFadingTransitions,
            navController = navController
        )
    }
}