import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import org.application.shikiapp.shared.App
import org.application.shikiapp.shared.AppConfig
import org.application.shikiapp.shared.di.AppContext
import org.application.shikiapp.shared.di.AppModuleInitializer
import org.application.shikiapp.shared.di.AppleContext
import platform.UIKit.UIViewController

@Suppress("unused")
fun MainViewController(): UIViewController {
    val config = AppConfig.createFlavorConfig("DarkShiki")
    AppContext.init(AppModuleInitializer(AppleContext(), config))

    return ComposeUIViewController(
        configure = { onFocusBehavior = OnFocusBehavior.DoNothing },
        content = { App() }
    )
}