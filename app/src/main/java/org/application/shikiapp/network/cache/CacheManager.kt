package org.application.shikiapp.network.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object CacheManager {
    private val mutex = Mutex()
    private val cache = mutableMapOf<String, CacheEntry<Any?>>()

    @Suppress("UNCHECKED_CAST")
    suspend fun <D> get(key: String) = mutex.withLock {
        cache[key] as? CacheEntry<D>
    }

    suspend fun <D> put(key: String, entry: CacheEntry<D>) {
        mutex.withLock {
            cache[key] = entry
        }
    }
}