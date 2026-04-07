package org.application.shikiapp.shared.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.Desktop
import java.net.URI

private class DesktopLinkHandler : IAction {
    override fun onOpenLink(url: String) {
        if (Desktop.isDesktopSupported()) {
            val desktop = Desktop.getDesktop()

            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(URI(url))
                } catch (_: Exception) {

                }
            } else {
                try {
                    Runtime.getRuntime().exec(arrayOf("xdg-open $url"))
                } catch (_: Exception) {

                }
            }
        }
    }
}

@Composable
actual fun rememberLinkHandler(): IAction = remember(::DesktopLinkHandler)