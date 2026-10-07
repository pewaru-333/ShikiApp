package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.application.shikiapp.shared.di.AppConfig
import org.application.shikiapp.shared.network.client.Network

internal actual fun isAuthLoginAvailable() = true

@Composable
internal actual fun preferAuthWebView() = Network.isYggdrasilEnabled

@Composable
internal actual fun AuthWebView(
    url: String,
    proxyPort: Int?,
    onCallback: (String) -> Unit,
    onError: () -> Unit
) {
    val webView = LocalAuthWebView.current
    if (webView != null) {
        webView(url, AppConfig.redirectUri, proxyPort, onCallback, onError)
    } else {
        LaunchedEffect(Unit) { onError() }
    }
}
