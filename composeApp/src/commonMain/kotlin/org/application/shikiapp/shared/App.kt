package org.application.shikiapp.shared

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.NavUri
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import io.ktor.http.*
import kotlinx.coroutines.launch
import org.application.shikiapp.shared.di.AppConfig
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.network.client.ApiRoutes
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.ui.theme.Theme
import org.application.shikiapp.shared.utils.AppLocale
import org.application.shikiapp.shared.utils.data.preferences.rememberPreference
import org.application.shikiapp.shared.utils.navigation.*
import org.application.shikiapp.shared.utils.ui.rememberLinkHandler

@Composable
fun App(navigator: NavHostController = rememberNavController()) {
    val scope = rememberCoroutineScope()

    val barVisibility = rememberNavigationBarVisibility()
    val deepLinkHandler = rememberDeepLinkHandler(navigator::navigate)

    val locale by rememberPreference { language }

    DisposableEffect(Unit) {
        ExternalUriHandler.listener = { uri ->
            scope.launch {
                try {
                    navigator.navigate(
                        deepLink = NavUri(uri),
                        navOptions = navOptions {
                            launchSingleTop = navigator.currentDestination?.hasRoute<Screen.AnimeLibLogin>() == true
                        }
                    )
                } catch (_: Exception) {

                }
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
    val navigate by rememberUpdatedState(onNavigate)

    return remember(linkHandler) {
        val scheme = Regex("^[A-Za-z][A-Za-z0-9+.-]*:")
        val origin = Url(AppConfig.baseUrl)

        object : UriHandler {
            override fun openUri(uri: String) {
                var link = uri

                try {
                    link = when {
                        uri.startsWith("//") -> "https:$uri"
                        scheme.containsMatchIn(uri) -> uri
                        else -> URLBuilder("${ApiRoutes.workingBaseUrl.trimEnd('/')}/")
                            .takeFrom(uri)
                            .buildString()
                    }

                    val builder = URLBuilder(link)
                    if (
                        (builder.protocol == URLProtocol.HTTP || builder.protocol == URLProtocol.HTTPS) &&
                        builder.encodedPath.trimEnd('/') != "/oauth/authorize"
                    ) {
                        val domains = buildList {
                            add(AppConfig.baseUrl)
                            addAll(AppConfig.urlMirrors)
                            addAll(Preferences.appUrlList)

                            val workingUrl = Network.baseUrlResolver.workingUrl
                            if (workingUrl != null) add(workingUrl)
                        }
                        val isInternal = domains.any { domain ->
                            try {
                                builder.host.equals(Url(domain.trim()).host, ignoreCase = true)
                            } catch (_: IllegalArgumentException) {
                                false
                            }
                        }

                        if (isInternal) {
                            builder.protocol = origin.protocol
                            builder.host = origin.host
                            builder.port = origin.port

                            val request = NavDeepLinkRequest.Builder
                                .fromUri(NavUri(builder.buildString()))
                                .build()

                            navigate(request)
                            return
                        }
                    }
                } catch (_: Exception) {

                }

                linkHandler.onOpenLink(link)
            }
        }
    }
}