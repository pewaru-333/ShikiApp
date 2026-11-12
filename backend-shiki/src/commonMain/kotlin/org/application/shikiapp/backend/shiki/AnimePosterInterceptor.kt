package org.application.shikiapp.backend.shiki

import coil3.intercept.Interceptor
import coil3.request.ImageResult
import coil3.request.SuccessResult
import io.ktor.http.Url
import io.ktor.http.fullPath
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.application.shikiapp.generated.shikiapp.AnimePosterQuery
import org.application.shikiapp.shared.network.client.Network
import kotlin.concurrent.Volatile
import kotlin.coroutines.cancellation.CancellationException

internal class AnimePosterInterceptor : Interceptor {
    private val posterPath = Regex("""/system/animes/original/([1-9]\d*)\.jpg""")
    private val mutex = Mutex()

    @Volatile
    private var posters: Map<String, String> = emptyMap()

    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val request = chain.request
        val path = request.data as? String ?: return chain.proceed()
        val id = posterPath.matchEntire(path)?.groupValues?.get(1) ?: return chain.proceed()
        val cached = posters[id]
        val result = if (cached == null) chain.proceed()
        else chain.withRequest(request.newBuilder().data(cached).build()).proceed()

        if (result is SuccessResult) {
            return result
        }

        val poster = try {
            loadPoster(id, path, cached)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            null
        }

        if (poster == null || poster == cached) return result
        return chain.withRequest(request.newBuilder().data(poster).build()).proceed()
    }

    private suspend fun loadPoster(id: String, path: String, failed: String?) = mutex.withLock {
        if (failed != null && posters[id] == failed) {
            posters -= id
        }

        val cached = posters[id]
        if (cached != null) return@withLock cached

        val poster = Network.apollo.query(AnimePosterQuery(id))
            .execute()
            .dataAssertNoErrors
            .animes
            .singleOrNull { it.id == id }
            ?.poster
            ?.originalUrl
            ?.takeIf { it.isNotBlank() && "missing" !in it }
            ?: return@withLock null

        val resolved = Url(poster).fullPath
        if (resolved == path) return@withLock null

        posters = buildMap {
            putAll(posters)

            if (size >= 128) {
                remove(keys.first())
            }

            put(id, resolved)
        }

        resolved
    }
}
