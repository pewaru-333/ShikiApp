@file:OptIn(ExperimentalComposeUiApi::class)

package org.application.shikiapp.shared


import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.v2.Window
import androidx.compose.ui.window.v2.rememberWindowState
import coil3.compose.setSingletonImageLoaderFactory
import okio.FileSystem
import org.application.shikiapp.shared.di.AppModule
import org.application.shikiapp.shared.di.Module
import org.application.shikiapp.shared.utils.initVlc
import org.application.shikiapp.shared.utils.navigation.DesktopDeepLink
import org.application.shikiapp.shared.utils.navigation.ExternalUriHandler
import org.application.shikiapp.shared.utils.sharedImageLoader
import org.application.shikiapp.shared.utils.ui.LocalAuthWebView
import org.application.shikiapp.shared.utils.ui.LocalWindowManager
import org.application.shikiapp.shared.utils.ui.rememberWindowManager
import org.jetbrains.compose.resources.stringResource
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val loginDeepLink = args.firstOrNull()
    if (loginDeepLink != null && DesktopDeepLink.tryForwardToRunningInstance(loginDeepLink)) {
        exitProcess(0)
    }

    DesktopDeepLink.registerUriSchemeIfNeeded(ProductServices.userAgent)
    initVlc()

    val services = ProductServices.create()
    AppModule.init(Module(services))

    application {
        val appIcon = rememberVectorPainter(ProductServices.appIcon)
        val windowState = rememberWindowState()

        setSingletonImageLoaderFactory { context ->
            sharedImageLoader(
                context = context,
                cacheDir = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / services.config.userAgent
            )
        }

        Tray(appIcon)
        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = stringResource(ProductServices.appName),
            icon = appIcon,
            content = {
                val windowManager = rememberWindowManager(windowState)

                CompositionLocalProvider(
                    LocalWindowManager provides windowManager,
                    LocalAuthWebView provides ProductServices.authWebView
                ) {
                    App()
                }

                LaunchedEffect(windowState.isInitialized) {
                    if (windowState.isInitialized) {
                        windowState.requestPlacement(WindowPlacement.Maximized)
                    }
                }

                LaunchedEffect(Unit) {
                    DesktopDeepLink.startInstanceListener { uri ->
                        windowState.requestMinimized(false)
                        window.toFront()

                        ExternalUriHandler.onNewUri(uri)
                    }
                }
            }
        )
    }
}