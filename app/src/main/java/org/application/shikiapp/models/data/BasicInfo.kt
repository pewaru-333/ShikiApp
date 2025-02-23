package org.application.shikiapp.models.data

import kotlinx.serialization.Serializable
import org.application.shikiapp.utils.BLANK

@Serializable
open class BasicInfo {
    val id: Long = 0L
    val name: String = BLANK
    val russian: String? = null
    val image: Image = Image()
    val url: String = BLANK
}