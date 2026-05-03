package org.application.shikiapp.shared.network.client

import coil3.intercept.Interceptor
import coil3.request.ImageResult

object ImageInterceptor : Interceptor {
    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val request = chain.request
        val data = request.data as? String ?: return chain.proceed()

        val newData = when {
            data.startsWith("/") -> ApiRoutes.workingBaseUrl + data
            data.startsWith("http://") -> data.replace("http://", "https://")
            else -> data
        }

        return chain.withRequest(request.newBuilder().data(newData).build()).proceed()
    }
}