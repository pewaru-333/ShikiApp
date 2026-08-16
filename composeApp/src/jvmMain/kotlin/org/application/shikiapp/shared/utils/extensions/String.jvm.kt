@file:OptIn(ExperimentalComposeUiApi::class)

package org.application.shikiapp.shared.utils.extensions

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import java.util.*

actual fun String.getLocaleLocalizedName(): String {
    val locale = Locale.forLanguageTag(this)
    return locale.getDisplayName(locale).replaceFirstChar(Char::uppercase)
}
actual fun String.toClipEntry() = ClipEntry(java.awt.datatransfer.StringSelection(this))
