package org.application.shikiapp.shared.network.cache

data class CacheEntry<out D>(
    val data: D,
    val etag: String?
)