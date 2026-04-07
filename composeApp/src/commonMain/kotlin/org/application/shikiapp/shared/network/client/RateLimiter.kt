package org.application.shikiapp.shared.network.client

import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// https://github.com/brudaswen/ktor-client-throttle
interface HttpRequestThrottler {
    suspend fun throttle(request: HttpRequestBuilder)
    suspend fun onResponse(response: HttpResponse): Boolean
}

class AppThrottler(
    private val rpsLimit: Int = 5,
    private val rpmLimit: Int = 90
) : HttpRequestThrottler {

    private val timestamps = mutableListOf<Long>()
    private val mutex = Mutex()

    override suspend fun throttle(request: HttpRequestBuilder) {
        var waitTime: Long

        do {
            waitTime = mutex.withLock {
                val now = System.currentTimeMillis()

                timestamps.removeAll { now - it > 60_000 }

                val inLastSecond = timestamps.count { now - it < 1_000 }
                val inLastMinute = timestamps.size

                if (inLastMinute >= rpmLimit) {
                    val oldest = timestamps.first()
                    60_000L - (now - oldest) + 50L
                } else if (inLastSecond >= rpsLimit) {
                    val oldestInSec = timestamps.filter { now - it < 1_000 }.minOrNull() ?: now
                    1_000L - (now - oldestInSec) + 10L
                } else {
                    timestamps.add(now)
                    0L
                }
            }

            if (waitTime > 0) {
                delay(waitTime)
            }
        } while (waitTime > 0)
    }

    override suspend fun onResponse(response: HttpResponse) = false
}

class HttpRequestThrottleConfig {
    var throttler: HttpRequestThrottler = AppThrottler()
}

val RateLimit = createClientPlugin("RateLimit", ::HttpRequestThrottleConfig) {
    on(Send) { request ->
        pluginConfig.throttler.throttle(request)

        val call = proceed(request)

        pluginConfig.throttler.onResponse(call.response)

        return@on call
    }
}