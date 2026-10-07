package org.application.shikiapp.auth.webview

import java.io.IOException
import java.net.*
import java.util.Collections

internal class AuthProxySelector(url: String, port: Int) : ProxySelector(), AutoCloseable {
    private val host = URI(url).host
    private val proxy = Collections.singletonList(Proxy(Proxy.Type.HTTP, InetSocketAddress("127.0.0.1", port)))
    private var previous: ProxySelector? = null

    fun install() {
        synchronized(selector) {
            check(selector.current == null)
            previous = getDefault()
            selector.previous = previous
            selector.current = this
            setDefault(selector)
        }
    }

    override fun close() {
        synchronized(selector) {
            if (selector.current !== this) return

            selector.current = null
            if (getDefault() === selector) setDefault(previous)
        }
    }

    override fun select(uri: URI): List<Proxy> {
        val hasScheme = uri.scheme == "http" || uri.scheme == "https"
        return if (hasScheme && uri.host.equals(host, ignoreCase = true)) {
            proxy
        } else {
            previous?.select(uri) ?: listOf(Proxy.NO_PROXY)
        }
    }

    override fun connectFailed(uri: URI, address: SocketAddress, error: IOException) {
        if (!uri.host.equals(host, ignoreCase = true)) {
            previous?.connectFailed(uri, address, error)
        }
    }

    companion object {
        private val selector = object : ProxySelector() {
            @Volatile
            var current: AuthProxySelector? = null

            @Volatile
            var previous: ProxySelector? = null

            override fun select(uri: URI): List<Proxy> =
                current?.select(uri) ?: previous?.select(uri) ?: listOf(Proxy.NO_PROXY)

            override fun connectFailed(uri: URI, address: SocketAddress, error: IOException) {
                val active = current
                if (active != null) active.connectFailed(uri, address, error)
                else previous?.connectFailed(uri, address, error)
            }
        }
    }
}
