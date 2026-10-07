package org.application.shikiapp.auth.webview

import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.concurrent.Worker
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.web.WebView
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.SwingUtilities

internal class DesktopAuthBrowser(
    url: String,
    redirectUri: String,
    proxyPort: Int?,
    private val onCallback: (String) -> Unit,
    private val onError: () -> Unit
) {
    val panel = JFXPanel()
    private val callbackUrl = redirectUri.substringBefore('?').substringBefore('#')
    private val closed = AtomicBoolean(false)
    private val proxy = proxyPort?.let { AuthProxySelector(url, it) }
    private var browser: WebView? = null
    private var session: AuthBrowserSession? = null
    private var completed = false
    private val locationListener = ChangeListener<String> { _, _, location ->
        val isCallback = location.substringBefore('?').substringBefore('#') == callbackUrl

        if (!closed.get() && !completed && isCallback) {
            completed = true
            browser?.engine?.loadWorker?.cancel()

            SwingUtilities.invokeLater {
                if (!closed.get()) {
                    onCallback(location)
                }
            }
        }
    }
    private val stateListener = ChangeListener<Worker.State> { _, _, state ->
        if (state == Worker.State.FAILED) fail()
    }

    init {
        AuthCallbackUrlHandlerProvider.register(redirectUri)
        Platform.setImplicitExit(false)
        Platform.runLater {
            if (!closed.get()) {
                try {
                    val loginSession = AuthBrowserSession()
                    session = loginSession
                    proxy?.install()

                    val view = WebView()
                    browser = view

                    view.engine.userDataDirectory = loginSession.userDataDirectory
                    view.engine.locationProperty().addListener(locationListener)
                    view.engine.loadWorker.stateProperty().addListener(stateListener)
                    view.engine.setCreatePopupHandler { view.engine }

                    panel.scene = Scene(view)
                    view.engine.load(url)
                } catch (_: Exception) {
                    fail()
                } catch (_: LinkageError) {
                    fail()
                }
            }
        }
    }

    fun close() {
        if (!closed.compareAndSet(false, true)) return

        Platform.runLater { proxy.use { releaseBrowser() } }
    }

    private fun fail() {
        if (closed.get() || completed) return
        completed = true

        try {
            releaseBrowser()
        } finally {
            proxy?.close()
            SwingUtilities.invokeLater { if (!closed.get()) onError() }
        }
    }

    private fun releaseBrowser() {
        try {
            browser?.engine?.let {
                it.locationProperty().removeListener(locationListener)
                it.loadWorker.stateProperty().removeListener(stateListener)
                it.loadWorker.cancel()
                it.load(null)
            }
        } finally {
            browser = null
            panel.scene = null
            session?.close()
            session = null
        }
    }
}
