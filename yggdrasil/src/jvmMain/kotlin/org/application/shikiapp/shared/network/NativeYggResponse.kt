package org.application.shikiapp.shared.network

import com.sun.jna.Pointer
import com.sun.jna.Structure

@Structure.FieldOrder(
    "statusCode",
    "headers",
    "body",
    "bodyLen",
    "error"
)
internal class NativeYggResponse(pointer: Pointer) : Structure(pointer) {
    @JvmField
    var statusCode: Long = 0

    @JvmField
    var headers: Pointer? = null

    @JvmField
    var body: Pointer? = null

    @JvmField
    var bodyLen: Long = 0

    @JvmField
    var error: Pointer? = null

    init {
        read()
    }
}