import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import coil3.compose.setSingletonImageLoaderFactory
import okio.Path.Companion.toPath
import org.application.shikiapp.shared.App
import org.application.shikiapp.shared.AppConfig
import org.application.shikiapp.shared.di.AppContext
import org.application.shikiapp.shared.di.AppModuleInitializer
import org.application.shikiapp.shared.di.AppleContext
import org.application.shikiapp.shared.utils.getCacheDirectory
import org.application.shikiapp.shared.utils.sharedImageLoader

@Suppress("unused")
fun MainViewController() = ComposeUIViewController(
    configure = { onFocusBehavior = OnFocusBehavior.DoNothing },
    content = {
        val config = AppConfig.createFlavorConfig("DarkShiki")
        AppContext.init(AppModuleInitializer(AppleContext(), config))

        setSingletonImageLoaderFactory { context ->
            sharedImageLoader(
                context = context,
                cacheDir = (getCacheDirectory() + "/ShikiApp_Cache").toPath()
            )
        }

        App()
    }
)