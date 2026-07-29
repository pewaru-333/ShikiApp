package org.application.shikiapp.shared.utils.extensions

import androidx.compose.ui.platform.ClipEntry

fun String?.toIntOrDefault(defaultValue: Int = 0) = this?.toIntOrNull() ?: defaultValue

fun String?.toDefaultValue(defaultValue: String = "0") = this ?: defaultValue

expect fun String.getLocaleLocalizedName(): String

expect fun String.toClipEntry(): ClipEntry