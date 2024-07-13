package org.application.shikiapp.network

import okhttp3.Interceptor
import okhttp3.Response
import org.application.shikiapp.utils.Preferences

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        if (Preferences.tokenExists())
            builder.header("User-Agent", "ShikiApp")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer ${Preferences.getToken()}")

        val request = builder.build()
        return chain.proceed(request)
    }
}