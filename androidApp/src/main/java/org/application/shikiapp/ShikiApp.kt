package org.application.shikiapp

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import okio.Path.Companion.toOkioPath
import org.application.shikiapp.shared.AppConfig
import org.application.shikiapp.shared.di.AppContext
import org.application.shikiapp.shared.di.AppModuleInitializer
import org.application.shikiapp.shared.utils.sharedImageLoader

class ShikiApp : Application(), SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()

        val config = AppConfig.createFlavorConfig(BuildConfig.USER_AGENT)
        val app = AppModuleInitializer(applicationContext, config)
        AppContext.init(app)
    }

    @OptIn(ExperimentalCoilApi::class)
    override fun newImageLoader(context: PlatformContext) = sharedImageLoader(
        context = context,
        cacheDir = context.cacheDir.toOkioPath(),
        components = { add(if (SDK_INT >= 28) AnimatedImageDecoder.Factory() else GifDecoder.Factory()) }
    )
}