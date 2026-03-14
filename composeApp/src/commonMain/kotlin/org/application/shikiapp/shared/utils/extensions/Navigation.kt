package org.application.shikiapp.shared.utils.extensions

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import org.application.shikiapp.shared.utils.navigation.Screen

fun NavHostController.onNavigationItemClick(route: Screen) {
    if (currentBackStackEntry.isSameRoute(route)) return

    val backStack = currentBackStack.value
    val index = backStack.indexOfLast { it.destination.hasRoute(route::class) }

    if (index != -1) {
        if (index == backStack.lastIndex) {
            return
        }

        navigate(route) {
            popUpTo(route::class) { inclusive = false }
            launchSingleTop = true
        }
    } else {
        navigate(route) {
            popUpTo(graph.findStartDestination().id) {
                saveState = true
            }

            launchSingleTop = true
            restoreState = true
        }
    }
}


fun NavBackStackEntry?.isTopLevelRouteSelected(route: Screen): Boolean {
    this ?: return false

    val hasRoute = destination.hierarchy.any { it.hasRoute(route::class) }
    if (!hasRoute) return false

    return when (route) {
        is Screen.UserRates -> {
            val args = runCatching { toRoute<Screen.UserRates>() }.getOrNull()
            args?.editable == route.editable
        }

        else -> true
    }
}

fun NavBackStackEntry?.isSameRoute(route: Screen): Boolean {
    this ?: return false

    if (!destination.hasRoute(route::class)) return false

    return when (route) {
        is Screen.Catalog -> {
            val args = runCatching { toRoute<Screen.Catalog>() }.getOrNull()
            args == route
        }

        is Screen.UserRates -> {
            val args = runCatching { toRoute<Screen.UserRates>() }.getOrNull()
            args?.editable == route.editable
        }

        else -> true
    }
}