@file:OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.shared.utils.ui

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.*
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitCancellation
import org.application.shikiapp.shared.network.client.Network

internal actual fun isAuthLoginAvailable() = true

@Composable
internal actual fun preferAuthWebView(): Boolean {
    if (Network.isYggdrasilEnabled) {
        return true
    }

    val context = LocalContext.current
    return remember(context) {
        val manager = context.getSystemService(UiModeManager::class.java)
        manager?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION ||
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal actual fun AuthWebView(
    url: String,
    proxyPort: Int?,
    onCallback: (String) -> Unit,
    onError: () -> Unit
) {
    val context = LocalContext.current

    val callback by rememberUpdatedState(onCallback)
    val error by rememberUpdatedState(onError)

    var ready by remember { mutableStateOf(false) }
    var browser by remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    var hasContent by remember { mutableStateOf(false) }
    var progress by remember { mutableIntStateOf(0) }

    val background = MaterialTheme.colorScheme.surface
    val contentAlpha by animateFloatAsState(
        targetValue = if (hasContent) 1f else 0f,
        animationSpec = tween(180)
    )

    fun releaseBrowser(view: WebView) {
        if (browser !== view) return

        browser = null
        canGoBack = false
        (view.parent as? ViewGroup)?.removeView(view)
        view.destroy()
    }

    fun fail() {
        if (failed) return

        failed = true
        error()
    }

    suspend fun displayBrowser() {
        hasContent = false
        progress = 0
        ready = true

        try {
            awaitCancellation()
        } finally {
            ready = false
            browser?.let(::releaseBrowser)
        }
    }

    LaunchedEffect(context, proxyPort) {
        ready = false
        try {
            AuthWebViewSession.withSession(context) {
                if (proxyPort == null) {
                    displayBrowser()
                } else {
                    AuthWebViewProxy.withProxy(context, proxyPort, ::displayBrowser)
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            fail()
        } catch (_: LinkageError) {
            fail()
        }
    }

    BackHandler(canGoBack && !failed) { browser?.goBack() }

    if (!ready || failed) {
        LoadingIndicator()
        return
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        AndroidView(
            onRelease = { (it as? WebView)?.let(::releaseBrowser) },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = contentAlpha },
            factory = {
                try {
                    WebView(it).apply {
                        browser = this
                        setBackgroundColor(background.toArgb())

                        clearCache(true)
                        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                        isFocusable = true
                        isFocusableInTouchMode = true

                        settings.allowContentAccess = false
                        settings.allowFileAccess = false
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.cacheMode = WebSettings.LOAD_NO_CACHE

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView, newProgress: Int) {
                                if (browser === view && !failed) {
                                    progress = newProgress
                                }
                            }
                        }
                        webViewClient = object : WebViewClient() {
                            private var completed = false

                            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                                if (browser !== view || failed || completed) return true

                                val target = request.url.toString()
                                if (isAuthCallback(target)) {
                                    completed = true
                                    callback(target)
                                    return true
                                }

                                val scheme = request.url.scheme
                                return scheme != "http" && scheme != "https"
                            }

                            override fun onPageStarted(view: WebView, startedUrl: String?, favicon: Bitmap?) {
                                if (browser === view && !failed && !completed) {
                                    progress = 0
                                }
                            }

                            override fun onPageFinished(view: WebView, finishedUrl: String?) {
                                if (browser !== view || failed || completed) return

                                progress = 100
                                canGoBack = view.canGoBack()
                                if (!hasContent) {
                                    view.postVisualStateCallback(0, object : WebView.VisualStateCallback() {
                                        override fun onComplete(requestId: Long) {
                                            if (browser === view && !failed && !completed) {
                                                hasContent = true
                                                view.requestFocus()
                                            }
                                        }
                                    })
                                }
                            }

                            override fun onReceivedError(view: WebView, request: WebResourceRequest, webError: WebResourceError) {
                                if (request.isForMainFrame && browser === view && !completed) fail()
                            }

                            override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
                                if (browser === view) {
                                    releaseBrowser(view)
                                    fail()
                                }
                                return true
                            }
                        }

                        loadUrl(url)
                    }
                } catch (_: Exception) {
                    browser?.let(::releaseBrowser)
                    ContextCompat.getMainExecutor(it).execute(::fail)
                    FrameLayout(it)
                } catch (_: LinkageError) {
                    browser?.let(::releaseBrowser)
                    ContextCompat.getMainExecutor(it).execute(::fail)
                    FrameLayout(it)
                }
            }
        )

        AnimatedVisibility(
            visible = !hasContent,
            enter = fadeIn(),
            exit = fadeOut(tween(180)),
            content = { LoadingIndicator() }
        )

        if (hasContent && progress < 100) {
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            )
        }
    }
}