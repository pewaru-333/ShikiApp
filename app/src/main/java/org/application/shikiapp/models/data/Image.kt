package org.application.shikiapp.models.data

import kotlinx.serialization.Serializable
import org.application.shikiapp.utils.BLANK

@Serializable
data class Image(
    val original: String = BLANK,
    val preview: String? = null,
    val x96: String? = null,
    val x48: String? = null
)