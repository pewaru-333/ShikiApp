package org.application.shikiapp.shared.network.client

class YggResponse(
    val statusCode: Int,
    val headersJson: String,
    val body: ByteArray
)
