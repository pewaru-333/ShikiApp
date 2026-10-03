package org.application.shikiapp

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import okio.Path.Companion.toOkioPath
import org.application.shikiapp.shared.di.AppModule
import org.application.shikiapp.shared.di.Module
import org.application.shikiapp.shared.utils.sharedImageLoader

class ShikiApp : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()

        AppModule.init(Module(applicationContext, ProductServices.create()))
    }

    override fun newImageLoader(context: PlatformContext) = sharedImageLoader(
        context = context,
        cacheDir = context.cacheDir.toOkioPath(),
        components = { add(if (SDK_INT >= 28) AnimatedImageDecoder.Factory() else GifDecoder.Factory()) }
    )
}