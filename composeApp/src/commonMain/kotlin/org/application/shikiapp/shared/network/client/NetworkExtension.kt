package org.application.shikiapp.shared.network.client

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import org.jetbrains.compose.resources.StringResource

interface NetworkExtension {
    val isActive: Boolean
    val requiresAuthProxy: Boolean
        get() = false
    val authUnavailableMessage: StringResource?
        get() = null

    fun createClient(normal: () -> HttpClient, block: HttpClientConfig<*>.() -> Unit): HttpClient
    suspend fun resolve(client: HttpClient): String
    suspend fun startAuthProxy(): Int?
    suspend fun stopAuthProxy()
}
