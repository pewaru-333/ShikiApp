package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

typealias AuthWebViewContent = @Composable (
    url: String,
    redirectUri: String,
    proxyPort: Int?,
    onCallback: (String) -> Unit,
    onError: () -> Unit
) -> Unit

val LocalAuthWebView = staticCompositionLocalOf<AuthWebViewContent?> { null }
