package org.application.shikiapp.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.TokenManager

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        if (Preferences.isTokenExists()) {
            if (Preferences.isTokenExpired()) CoroutineScope(Dispatchers.IO).launch {
                TokenManager.refreshToken()
            }
            builder.header("User-Agent", "ShikiApp")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer ${Preferences.getToken()}")
        }

        val request = builder.build()
        return chain.proceed(request)
    }
}