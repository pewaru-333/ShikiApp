@file:OptIn(ExperimentalCoilApi::class)


import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import okio.FileSystem
import org.application.shikiapp.shared.App
import org.application.shikiapp.shared.AppConfig
import org.application.shikiapp.shared.di.AppContext
import org.application.shikiapp.shared.di.AppModuleInitializer
import org.application.shikiapp.shared.di.DesktopContext
import org.application.shikiapp.shared.utils.navigation.DesktopDeepLink
import org.application.shikiapp.shared.utils.navigation.ExternalUriHandler
import org.application.shikiapp.shared.utils.sharedImageLoader
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.app_name
import shikiapp.composeapp.generated.resources.ic_launcher
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val loginDeepLink = args.firstOrNull()

    if (loginDeepLink != null && DesktopDeepLink.tryForwardToRunningInstance(loginDeepLink)) {
        exitProcess(0)
    }

    DesktopDeepLink.registerUriSchemeIfNeeded()

    application {
        val app = AppModuleInitializer(DesktopContext(), AppConfig.createDesktopConfig())
        AppContext.init(app)

        val windowState = rememberWindowState()

        setSingletonImageLoaderFactory { context ->
            sharedImageLoader(
                context = context,
                cacheDir = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "ShikiApp_Cache"
            )
        }

        Tray(
            icon = painterResource(Res.drawable.ic_launcher)
        )

        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = stringResource(Res.string.app_name),
            icon = painterResource(Res.drawable.ic_launcher),
            content = {
                LaunchedEffect(Unit) {
                    DesktopDeepLink.startInstanceListener { uri ->
                        windowState.isMinimized = false
                        window.toFront()

                        ExternalUriHandler.onNewUri(uri)
                    }
                }

                App()
            }
        )
    }
}