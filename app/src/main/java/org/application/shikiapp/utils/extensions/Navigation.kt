package org.application.shikiapp.utils.extensions

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import kotlin.reflect.KClass

@SuppressLint("RestrictedApi")
fun NavController.safeDeepLink(intent: Intent?) {
    if (intent == null || intent.action != Intent.ACTION_VIEW) {
        return
    }

    val uri = intent.data ?: return
    val request = NavDeepLinkRequest.Builder.fromUri(uri).setAction(Intent.ACTION_VIEW).build()
    val match = graph.matchDeepLink(request)

    if (match != null) {
        val destination = match.destination

        val intent = Intent().apply {
            setDataAndType(request.uri, request.mimeType)
            action = request.action
        }

        val newArgs = (destination.addInDefaultArgs(match.matchingArgs) ?: Bundle()).apply {
            putParcelable(NavController.KEY_DEEP_LINK_INTENT, intent)
        }

        navigate(destination.id, newArgs)
    }
}

fun NavHostController.toBottomBarItem(route: Any) = currentBackStackEntry?.destination?.route?.let {
    if (!route.toString().contains(it)) {
        navigate(route) {
            launchSingleTop = true
            restoreState = true
            popBackStack(route, true)
        }
    }
}

fun <T : Any> NavBackStackEntry?.isCurrentRoute(route: KClass<T>) =
    this?.destination?.hierarchy?.any { it.hasRoute(route) } == true