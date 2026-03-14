package org.application.shikiapp.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavHostController
import androidx.navigation.NavUri
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.ui.theme.Theme
import org.application.shikiapp.shared.utils.AppLocale
import org.application.shikiapp.shared.utils.extensions.toFullUri
import org.application.shikiapp.shared.utils.navigation.ExternalUriHandler
import org.application.shikiapp.shared.utils.navigation.LocalBarVisibility
import org.application.shikiapp.shared.utils.navigation.Navigation
import org.application.shikiapp.shared.utils.navigation.rememberNavigationBarVisibility
import org.application.shikiapp.shared.utils.ui.rememberLinkHandler

@Composable
fun App(navigator: NavHostController = rememberNavController()) {
    val scope = rememberCoroutineScope()

    val barVisibility = rememberNavigationBarVisibility()
    val deepLinkHandler = rememberDeepLinkHandler(navigator::navigate)

    val locale by Preferences.languageFlow.collectAsStateWithLifecycle(Preferences.language)

    DisposableEffect(Unit) {
        ExternalUriHandler.listener = { uri ->
            scope.launch {
                navigator.navigate(NavUri(uri))
            }
        }

        onDispose { ExternalUriHandler.listener = null }
    }

    CompositionLocalProvider(
        content = { Theme { Navigation(navigator) } },
        values = arrayOf(
            AppLocale provides locale,
            LocalBarVisibility provides barVisibility,
            LocalUriHandler provides deepLinkHandler
        )
    )
}

@Composable
private fun rememberDeepLinkHandler(onNavigate: (NavDeepLinkRequest) -> Unit): UriHandler {
    val linkHandler = rememberLinkHandler()

    return remember {
        object : UriHandler {
            override fun openUri(uri: String) {
                val link = uri.toFullUri().toString()

                if (link.contains("oauth/authorize")) {
                    linkHandler.onOpenLink(link)
                    return
                }

                try {
                    val request = NavDeepLinkRequest.Builder
                        .fromUri(NavUri(link))
                        .build()

                    onNavigate(request)
                } catch (_: Exception) {
                    linkHandler.onOpenLink(link)
                }
            }
        }
    }
}