package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.application.shikiapp.utils.BLANK

@Serializable
data class Token(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Long,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("scope") val scope: String,
    @SerialName("created_at") val createdAt: Long
) {
    constructor(accessToken: String, refreshToken: String) : this(
        accessToken = accessToken,
        refreshToken = refreshToken,
        tokenType = BLANK,
        scope = BLANK,
        createdAt = 0L,
        expiresIn = 0L
    )

    companion object {
        val empty = Token(BLANK, BLANK, 0, BLANK, BLANK, 0)
    }
}