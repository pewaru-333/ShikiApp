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
import androidx.compose.ui.window.v2.WindowBoundsProvider
import androidx.compose.ui.window.v2.WindowPositionProvider
import androidx.compose.ui.window.v2.rememberWindowState
import coil3.compose.setSingletonImageLoaderFactory
import okio.FileSystem
import org.application.shikiapp.shared.di.AppContext
import org.application.shikiapp.shared.di.AppModuleInitializer
import org.application.shikiapp.shared.di.DesktopContext
import org.application.shikiapp.shared.utils.initVlc
import org.application.shikiapp.shared.utils.navigation.DesktopDeepLink
import org.application.shikiapp.shared.utils.navigation.ExternalUriHandler
import org.application.shikiapp.shared.utils.sharedImageLoader
import org.application.shikiapp.shared.utils.ui.LocalWindowManager
import org.application.shikiapp.shared.utils.ui.rememberWindowManager
import org.jetbrains.compose.resources.stringResource
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val userAgent = System.getProperty("app.userAgent", "ShikiApp")

    val loginDeepLink = args.firstOrNull()
    if (loginDeepLink != null && DesktopDeepLink.tryForwardToRunningInstance(loginDeepLink)) {
        exitProcess(0)
    }

    DesktopDeepLink.registerUriSchemeIfNeeded(userAgent)
    initVlc()

    application {
        val (appConfig, desktopConfig) = AppConfig.createDesktopConfig(userAgent)
        AppContext.init(AppModuleInitializer(DesktopContext(), appConfig))

        val appIcon = rememberVectorPainter(desktopConfig.appIcon)
        val windowState = rememberWindowState(
            initialPlacement = WindowPlacement.Maximized,
            initialBoundsProvider = WindowBoundsProvider(
                positionProvider = WindowPositionProvider.CenteredOnScreen
            )
        )

        setSingletonImageLoaderFactory { context ->
            sharedImageLoader(
                context = context,
                cacheDir = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / appConfig.userAgent
            )
        }

        Tray(appIcon)
        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = stringResource(desktopConfig.appName),
            icon = appIcon,
            content = {
                val windowManager = rememberWindowManager(windowState, window)

                CompositionLocalProvider(LocalWindowManager provides windowManager) {
                    App()
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