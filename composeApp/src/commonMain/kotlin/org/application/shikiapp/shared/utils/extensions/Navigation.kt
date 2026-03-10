package org.application.shikiapp.shared.utils.extensions

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import kotlin.reflect.KClass

fun NavHostController.toBottomBarItem(route: Any) =
    currentBackStackEntry?.destination?.route?.let {
        if (!route.toString().contains(it)) {
            navigate(route) {
                launchSingleTop = true
                restoreState = true
                popBackStack(route, true)
            }
        }
    }

fun NavBackStackEntry?.isCurrentRoute(route: KClass<*>) =
    this?.destination?.hierarchy?.any { it.hasRoute(route) } == true