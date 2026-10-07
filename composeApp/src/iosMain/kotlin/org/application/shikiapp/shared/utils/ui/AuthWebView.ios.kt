@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package org.application.shikiapp.shared.utils.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.readValue
import kotlinx.cinterop.useContents
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.webviewproxy.ShikiConfigureWebViewProxy
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.Foundation.NSError
import platform.Foundation.NSProcessInfo
import platform.WebKit.*
import platform.darwin.NSObject

internal actual fun isAuthLoginAvailable(): Boolean =
    !Network.isYggdrasilEnabled || NSProcessInfo.processInfo.operatingSystemVersion.useContents {
        majorVersion >= 17
    }

@Composable
internal actual fun preferAuthWebView() = true

@Composable
internal actual fun AuthWebView(
    url: String,
    proxyPort: Int?,
    onCallback: (String) -> Unit,
    onError: () -> Unit
) {
    val callback by rememberUpdatedState(onCallback)
    val error by rememberUpdatedState(onError)
    val delegate = remember {
        object : NSObject(), WKNavigationDelegateProtocol {
            override fun webView(
                webView: WKWebView,
                decidePolicyForNavigationAction: WKNavigationAction,
                decisionHandler: (WKNavigationActionPolicy) -> Unit
            ) {
                val target = decidePolicyForNavigationAction.request.URL?.absoluteString
                if (target != null && isAuthCallback(target)) {
                    decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
                    callback(target)
                } else {
                    decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
                }
            }

            override fun webView(webView: WKWebView, didFailProvisionalNavigation: WKNavigation?, withError: NSError) {
                error()
            }

            override fun webViewWebContentProcessDidTerminate(webView: WKWebView) {
                error()
            }
        }
    }

    val browser = remember(url, proxyPort) {
        val configuration = WKWebViewConfiguration().apply {
            websiteDataStore = WKWebsiteDataStore.nonPersistentDataStore()
        }

        if (proxyPort != null && !ShikiConfigureWebViewProxy(configuration, proxyPort)) {
            null
        } else {
            WKWebView(CGRectZero.readValue(), configuration).apply {
                navigationDelegate = delegate
                loadRequest(NSURLRequest(NSURL(string = url)))
            }
        }
    }

    if (browser == null) {
        LaunchedEffect(Unit) { error() }
        return
    }

    DisposableEffect(browser) {
        onDispose {
            browser.stopLoading()
            browser.navigationDelegate = null
        }
    }

    UIKitView(
        factory = { browser },
        modifier = Modifier.fillMaxSize()
    )
}
