package org.application.shikiapp.shared.network.client

import kotlin.io.encoding.Base64

data class ProxyConfig(
    val host: String,
    val port: Int,
    val user: String,
    val pass: String,
    val isSocks: Boolean,
    val isHttps: Boolean
) {
    val hasAuth: Boolean get() = user.isNotBlank() && pass.isNotBlank()

    val httpAuthHeader: String? = if (isSocks || !hasAuth) null
    else "Basic ${Base64.encode("${user}:${pass}".encodeToByteArray())}"

    companion object {
        fun create(enabled: Boolean, host: String, port: String, user: String, pass: String): ProxyConfig? {
            if (!enabled) return null

            if (host.isBlank()) return null
            val portInt = port.toIntOrNull() ?: return null

            val isSocks = host.startsWith("socks5://", ignoreCase = true) || host.startsWith("socks://", ignoreCase = true)
            val isHttps = host.startsWith("https://", ignoreCase = true)

            val cleanHost = host
                .substringAfter("://")
                .substringBefore("/")
                .substringBefore(":")

            return ProxyConfig(
                host = cleanHost,
                port = portInt,
                user = user.trim(),
                pass = pass.trim(),
                isSocks = isSocks,
                isHttps = isHttps
            )
        }
    }
}
