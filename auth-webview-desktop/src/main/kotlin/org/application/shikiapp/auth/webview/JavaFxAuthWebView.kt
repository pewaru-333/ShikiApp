package org.application.shikiapp.auth.webview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import java.util.concurrent.atomic.AtomicReference
import javax.swing.JPanel
import javax.swing.SwingUtilities

@Composable
fun JavaFxAuthWebView(
    url: String,
    redirectUri: String,
    proxyPort: Int?,
    onCallback: (String) -> Unit,
    onError: () -> Unit
) {
    val callback by rememberUpdatedState(onCallback)
    val error by rememberUpdatedState(onError)
    val holder = remember(url, redirectUri, proxyPort) { AtomicReference<DesktopAuthBrowser>() }

    DisposableEffect(holder) {
        onDispose {
            holder.getAndSet(null)?.close()
        }
    }

    key(holder) {
        SwingPanel(
            modifier = Modifier.fillMaxSize(),
            factory = {
                try {
                    DesktopAuthBrowser(url, redirectUri, proxyPort, callback, error)
                        .also(holder::set)
                        .panel
                } catch (_: Exception) {
                    SwingUtilities.invokeLater { error() }
                    JPanel()
                } catch (_: LinkageError) {
                    SwingUtilities.invokeLater { error() }
                    JPanel()
                }
            }
        )
    }
}
