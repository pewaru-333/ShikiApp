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
        .respectCacheHeaders(false)
        .error(R.drawable.vector_bad)
        .fallback(R.drawable.vector_bad)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .memoryCache {
            MemoryCache.Builder(this)
                .maxSizePercent(0.2)
                .strongReferencesEnabled(true)
                .build()
        }
        .diskCachePolicy(CachePolicy.ENABLED)
        .diskCache {
            DiskCache.Builder()
                .maxSizeBytes(Preferences.getCache() * 1024 * 1024L)
                .directory(cacheDir)
                .build()
        }.build()
}