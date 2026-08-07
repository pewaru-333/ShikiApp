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
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import okio.FileSystem
import org.application.shikiapp.shared.di.AppContext
import org.application.shikiapp.shared.di.AppModuleInitializer
import org.application.shikiapp.shared.di.DesktopContext
import org.application.shikiapp.shared.ui.theme.Icons
import org.application.shikiapp.shared.utils.initVlc
import org.application.shikiapp.shared.utils.navigation.DesktopDeepLink
import org.application.shikiapp.shared.utils.navigation.ExternalUriHandler
import org.application.shikiapp.shared.utils.sharedImageLoader
import org.application.shikiapp.shared.utils.ui.LocalWindowManager
import org.application.shikiapp.shared.utils.ui.rememberWindowManager
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.app_name
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val loginDeepLink = args.firstOrNull()

    if (loginDeepLink != null && DesktopDeepLink.tryForwardToRunningInstance(loginDeepLink)) {
        exitProcess(0)
    }

    DesktopDeepLink.registerUriSchemeIfNeeded()
    initVlc()

    application {
        val app = AppModuleInitializer(DesktopContext(), AppConfig.createDesktopConfig())
        AppContext.init(app)

        val windowState = rememberWindowState(
            initialPlacement = WindowPlacement.Maximized,
            initialBoundsProvider = WindowBoundsProvider(
                positionProvider = WindowPositionProvider.CenteredOnScreen
            )
        )

        setSingletonImageLoaderFactory { context ->
            sharedImageLoader(
                context = context,
                cacheDir = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "ShikiApp_Cache"
            )
        }

        Tray(
            icon = rememberVectorPainter(Icons.AppIcon)
        )

        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = stringResource(Res.string.app_name),
            icon = rememberVectorPainter(Icons.AppIcon),
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