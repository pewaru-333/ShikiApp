import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import coil3.compose.setSingletonImageLoaderFactory
import okio.Path.Companion.toPath
import org.application.shikiapp.shared.App
import org.application.shikiapp.shared.di.AppModule
import org.application.shikiapp.shared.di.Module
import org.application.shikiapp.shared.utils.getCacheDirectory
import org.application.shikiapp.shared.utils.sharedImageLoader
import platform.UIKit.UIViewController

@Suppress("unused")
fun MainViewController(): UIViewController {
    AppModule.init(Module(ProductServices.create()))

    return ComposeUIViewController(
        configure = { onFocusBehavior = OnFocusBehavior.DoNothing },
        content = {
            setSingletonImageLoaderFactory { context ->
                sharedImageLoader(
                    context = context,
                    cacheDir = (getCacheDirectory() + "/ShikiApp_Cache").toPath()
                )
            }

            App()
        }
    )
}
