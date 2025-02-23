package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class BasicContent : BasicInfo() {
    @SerialName("kind")
    val kind: String? = null

    @SerialName("score")
    val score: String? = null

    @SerialName("status")
    val status: String? = null

    @SerialName("aired_on")
    val airedOn: String? = null

    @SerialName("released_on")
    val releasedOn: String? = null
}