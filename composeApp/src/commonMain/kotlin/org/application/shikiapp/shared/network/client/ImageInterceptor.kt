package org.application.shikiapp.shared.network.client

import coil3.intercept.Interceptor
import coil3.request.ImageResult
import coil3.request.SuccessResult
import io.ktor.http.URLProtocol
import io.ktor.http.Url

object ImageInterceptor : Interceptor {
    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val request = chain.request
        val data = request.data as? String ?: return chain.proceed()

        val newData = when {
            data.startsWith("//") -> "https:$data"
            data.startsWith("/") -> Network.baseUrlResolver.resolve() + data
            data.startsWith("http://[") -> data
            data.startsWith("http://") -> "https:${data.removePrefix("http:")}"
            else -> data
        }
        val result = if (newData == data) chain.proceed()
        else chain.withRequest(request.newBuilder().data(newData).build()).proceed()

        if (result is SuccessResult) {
            return result
        }

        val url = try {
            Url(newData)
        } catch (_: IllegalArgumentException) {
            return result
        }

        if (!url.encodedPath.startsWith("/camo/")) {
            return result
        }

        val original = url.parameters["url"] ?: return result
        if (
            !original.startsWith("https://", ignoreCase = true) &&
            !original.startsWith("http://", ignoreCase = true)
        ) return result

        val originalUrl = try {
            Url(original)
        } catch (_: IllegalArgumentException) {
            return result
        }

        if (
            (originalUrl.protocol != URLProtocol.HTTP && originalUrl.protocol != URLProtocol.HTTPS) ||
            originalUrl.host.isBlank() || original == newData
        ) return result

        return chain.withRequest(request.newBuilder().data(original).build()).proceed()
    }
}