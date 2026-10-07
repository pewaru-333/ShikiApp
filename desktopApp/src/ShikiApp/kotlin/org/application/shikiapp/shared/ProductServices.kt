package org.application.shikiapp.shared

import org.application.shikiapp.shared.ui.theme.Icons
import org.application.shikiapp.shared.utils.ui.AuthWebViewContent
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.app_name
import org.application.shikiapp.backend.shiki.ProductServices as ShikiServices

internal object ProductServices {
    const val userAgent = "ShikiApp"
    val appName = Res.string.app_name
    val appIcon get() = Icons.AppIcon
    val authWebView: AuthWebViewContent? = null

    fun create() = ShikiServices.create(redirectUri = "app://login/")
}
