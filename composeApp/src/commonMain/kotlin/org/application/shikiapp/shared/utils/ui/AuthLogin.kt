@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.shared.utils.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.application.shikiapp.shared.di.AppConfig
import org.application.shikiapp.shared.network.client.ApiRoutes
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.utils.getFullscreenDialogProperties
import org.application.shikiapp.shared.utils.navigation.ExternalUriHandler
import org.application.shikiapp.shared.utils.rememberToastState
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_dismiss
import shikiapp.composeapp.generated.resources.text_error_open_link
import shikiapp.composeapp.generated.resources.text_login

@Composable
fun rememberAuthLauncher(uriHandler: UriHandler): AuthLauncher {
    val mutex = remember(::Mutex)
    val scope = rememberCoroutineScope()
    val toast = rememberToastState()
    val isWebView = preferAuthWebView()

    var active by remember { mutableStateOf(false) }
    var ready by remember { mutableStateOf(false) }
    var proxyPort by remember { mutableStateOf<Int?>(null) }

    val visibility = remember { MutableTransitionState(false) }
    val dialogVisible = visibility.currentState || visibility.targetState || !visibility.isIdle

    LaunchedEffect(active) {
        visibility.targetState = active
    }

    LaunchedEffect(dialogVisible) {
        if (!dialogVisible) return@LaunchedEffect

        snapshotFlow { visibility.isIdle && visibility.currentState }
            .first { it }

        mutex.withLock {
            ready = false

            try {
                proxyPort = Network.startAuthProxy()
                ready = true
                awaitCancellation()
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                active = false
                toast.onShow(Res.string.text_error_open_link)
            } finally {
                ready = false
                withContext(NonCancellable) {
                    try {
                        Network.stopAuthProxy()
                    } catch (_: Exception) {
                        toast.onShow(Res.string.text_error_open_link)
                    }
                }
            }
        }
    }

    if (dialogVisible) {
        Dialog(
            onDismissRequest = { active = false },
            properties = getFullscreenDialogProperties()
        ) {
            AnimatedVisibility(
                visibleState = visibility,
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(180))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .safeDrawingPadding()
                        .imePadding()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                            .heightIn(min = 64.dp)
                            .padding(16.dp, 8.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.text_login),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge
                        )
                        TextButton(
                            onClick = { active = false },
                            content = { Text(stringResource(Res.string.text_dismiss)) }
                        )
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        if (ready) {
                            AuthWebView(
                                url = ApiRoutes.authUri,
                                proxyPort = proxyPort,
                                onCallback = { url ->
                                    scope.launch {
                                        if (active) {
                                            active = false
                                            ExternalUriHandler.onNewUri(url)
                                        }
                                    }
                                },
                                onError = {
                                    scope.launch {
                                        if (active) {
                                            active = false
                                            toast.onShow(Res.string.text_error_open_link)
                                        }
                                    }
                                }
                            )
                        } else {
                            LoadingIndicator()
                        }
                    }
                }
            }
        }
    }

    return object : AuthLauncher {
        override val usesWebView = isWebView
        override val isAvailable = isAuthLoginAvailable()
        override val isLaunching get() = active || dialogVisible

        override fun launch() {
            if (!isAvailable || active || dialogVisible) return
            if (isWebView) active = true
            else uriHandler.openUri(ApiRoutes.authUri)
        }
    }
}

internal fun isAuthCallback(url: String): Boolean =
    url.substringBefore('?').substringBefore('#') ==
        AppConfig.redirectUri.substringBefore('?').substringBefore('#')

internal expect fun isAuthLoginAvailable(): Boolean

@Composable
internal expect fun preferAuthWebView(): Boolean

@Composable
internal expect fun AuthWebView(
    url: String,
    proxyPort: Int?,
    onCallback: (String) -> Unit,
    onError: () -> Unit
)
