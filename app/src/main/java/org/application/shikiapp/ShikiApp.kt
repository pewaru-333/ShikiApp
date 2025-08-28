package org.application.shikiapp

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.memory.MemoryCache
import coil3.network.NetworkFetcher
import coil3.request.CachePolicy
import coil3.request.crossfade
import org.application.shikiapp.network.client.CoilClient
import org.application.shikiapp.network.client.ImageInterceptor
import org.application.shikiapp.utils.Preferences

class ShikiApp : Application(), SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()

        Preferences.getInstance(this)
    }

    @OptIn(ExperimentalCoilApi::class)
    override fun newImageLoader(context: PlatformContext) = ImageLoader(context).newBuilder()
        .components {
            add(NetworkFetcher.Factory({ CoilClient }))
            add(ImageInterceptor)
            if (SDK_INT >= 28) {
                add(AnimatedImageDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .error(getDrawable(R.drawable.vector_bad)?.asImage())
        .fallback(getDrawable(R.drawable.vector_bad)?.asImage())
        .crossfade(200)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, 0.25)
                .build()
        }
        .diskCachePolicy(CachePolicy.ENABLED)
        .diskCache {
            DiskCache.Builder()
                .maxSizeBytes(Preferences.cache.toLong() * 1024 * 1024L)
                .directory(context.cacheDir)
                .build()
        }
        .build()
}