package org.application.shikiapp.auth.webview

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.net.spi.URLStreamHandlerProvider

class AuthCallbackUrlHandlerProvider : URLStreamHandlerProvider() {
    override fun createURLStreamHandler(protocol: String): URLStreamHandler? {
        if (!protocol.equals(scheme, ignoreCase = true) ||
            protocol == "http" ||
            protocol == "https"
        ) return null

        return object : URLStreamHandler() {
            override fun openConnection(url: URL): URLConnection = object : URLConnection(url) {
                override fun connect() {
                    connected = true
                }

                override fun getContentType() = "text/html"

                override fun getInputStream(): InputStream {
                    connect()

                    return ByteArrayInputStream(byteArrayOf())
                }
            }
        }
    }

    companion object {
        @Volatile
        private var scheme: String? = null

        internal fun register(redirectUri: String) {
            scheme = URI(redirectUri).scheme
        }
    }
}
