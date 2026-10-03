package org.application.shikiapp.shared.network

import com.sun.jna.Library
import com.sun.jna.Pointer

internal interface YggbridgeNative : Library {
    fun YggStart(peersJSON: String, savedPrivateKeyPEM: String): Pointer?

    fun YggStop(): Pointer?

    fun YggIsStarted(): Int

    fun YggAddress(): Pointer?

    fun YggPrivateKeyPEM(): Pointer?

    fun YggRequest(
        method: String,
        url: String,
        headersJSON: String,
        body: Pointer?,
        bodyLen: Long,
        timeoutMillis: Long
    ): Pointer?

    fun YggFreeString(value: Pointer?)

    fun YggFreeResponse(response: Pointer?)
}