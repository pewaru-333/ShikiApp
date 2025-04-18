package org.application.shikiapp

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import org.application.shikiapp.utils.Preferences

class ShikiApp : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()

        Preferences.getInstance(this)
    }

    override fun newImageLoader() = ImageLoader(this).newBuilder()
        .error(R.drawable.vector_bad)
        .fallback(R.drawable.vector_bad)
        .crossfade(200)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .memoryCache {
            MemoryCache.Builder(this)
                .maxSizePercent(0.3)
                .strongReferencesEnabled(true)
                .build()
        }
        .diskCachePolicy(CachePolicy.ENABLED)
        .diskCache {
            DiskCache.Builder()
                .maxSizeBytes(Preferences.cache * 1024 * 1024L)
                .directory(cacheDir)
                .build()
        }.build()
}