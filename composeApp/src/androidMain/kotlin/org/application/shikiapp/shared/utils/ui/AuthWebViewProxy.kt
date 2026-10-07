package org.application.shikiapp.shared.utils.ui

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.webkit.ProxyConfig
import androidx.webkit.ProxyController
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

internal object AuthWebViewProxy {
    private val mutex = Mutex()

    suspend fun withProxy(context: Context, port: Int, block: suspend () -> Unit) {
        mutex.withLock {
            val controller = ProxyController.getInstance()
            val executor = ContextCompat.getMainExecutor(context)
            try {
                suspendCancellableCoroutine { continuation ->
                    val config = ProxyConfig.Builder()
                        .addProxyRule("127.0.0.1:$port")
                        .removeImplicitRules()
                        .build()

                    controller.setProxyOverride(config, executor) {
                        if (continuation.isActive) continuation.resume(Unit)
                    }
                }
                block()
            } finally {
                withContext(NonCancellable) {
                    suspendCancellableCoroutine { continuation ->
                        controller.clearProxyOverride(executor) {
                            if (continuation.isActive) continuation.resume(Unit)
                        }
                    }
                }
            }
        }
    }
}
