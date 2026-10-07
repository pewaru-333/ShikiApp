package org.application.shikiapp.auth.webview

import java.io.File
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.URI

internal class AuthBrowserSession : AutoCloseable {
    private val profile: AuthBrowserProfile
    val userDataDirectory: File get() = profile.directory
    private val previous: CookieHandler?
    private var closed = false

    init {
        synchronized(cookies) {
            check(!cookies.enabled)

            profile = AuthBrowserProfile()
            previous = CookieHandler.getDefault()

            cookies.cookieStore.removeAll()
            cookies.enabled = true

            CookieHandler.setDefault(cookies)
        }
    }

    override fun close() {
        synchronized(cookies) {
            if (closed) return
            closed = true
            cookies.enabled = false
            cookies.cookieStore.removeAll()
            if (CookieHandler.getDefault() === cookies) CookieHandler.setDefault(previous)
        }

        profile.close()
    }

    companion object {
        private val cookies = object : CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER) {
            var enabled = false

            @Synchronized
            override fun get(uri: URI, requestHeaders: Map<String, List<String>>): Map<String, List<String>> {
                if (!enabled) return emptyMap()

                val scheme = when (uri.scheme) {
                    "javascripts" -> "https"
                    "javascript" -> "http"
                    else -> return super.get(uri, requestHeaders)
                }

                val origin = URI("$scheme:${uri.rawSchemeSpecificPart}")
                val visible = CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER)

                for (cookie in cookieStore.get(origin)) {
                    if (cookie.isHttpOnly) continue
                    visible.cookieStore.add(origin, cookie)
                }

                return visible.get(origin, requestHeaders)
            }

            @Synchronized
            override fun put(uri: URI, responseHeaders: Map<String, List<String>>) {
                if (enabled) super.put(uri, responseHeaders)
            }
        }
    }
}
