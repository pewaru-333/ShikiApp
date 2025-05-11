package org.application.shikiapp.network.client

import coil3.intercept.Interceptor
import coil3.request.ImageResult
import org.application.shikiapp.utils.BASE_URL

object ImageInterceptor : Interceptor {
    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val request = chain.request
        val data = request.data

        if (data is String && data.startsWith("/")) {
            val newRequest = request.newBuilder().data(BASE_URL + data).build()
            val newChain = chain.withRequest(newRequest)

            return newChain.proceed()
        }

        return chain.proceed()
    }
}