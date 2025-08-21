package org.application.shikiapp.network.cache

data class CacheEntry<out D>(
    val data: D,
    val etag: String?
)