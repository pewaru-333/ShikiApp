package org.application.shikiapp.shared.utils.ui

import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebStorage
import androidx.core.content.ContextCompat
import androidx.webkit.WebStorageCompat
import androidx.webkit.WebViewFeature
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

internal object AuthWebViewSession {
    private val mutex = Mutex()

    suspend fun withSession(context: Context, block: suspend () -> Unit) {
        mutex.withLock {
            try {
                withContext(NonCancellable) { clearData(context) }
                block()
            } finally {
                withContext(NonCancellable) { clearData(context) }
            }
        }
    }

    private suspend fun clearData(context: Context) {
        val storage = WebStorage.getInstance()

        if (WebViewFeature.isFeatureSupported(WebViewFeature.DELETE_BROWSING_DATA)) {
            suspendCancellableCoroutine { continuation ->
                WebStorageCompat.deleteBrowsingData(storage, ContextCompat.getMainExecutor(context)) {
                    if (continuation.isActive) continuation.resume(Unit)
                }
            }
        } else {
            suspendCancellableCoroutine { continuation ->
                CookieManager.getInstance().removeAllCookies {
                    if (continuation.isActive) continuation.resume(Unit)
                }
            }
            storage.deleteAllData()
        }
    }
}
