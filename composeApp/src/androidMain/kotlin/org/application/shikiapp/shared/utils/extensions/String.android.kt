package org.application.shikiapp.shared.utils.extensions

import androidx.compose.ui.platform.toClipEntry
import java.util.Locale

actual fun String.getLocaleLocalizedName(): String {
    val locale = Locale.forLanguageTag(this)
    return locale.getDisplayName(locale).replaceFirstChar(Char::uppercase)
}

actual fun String.toClipEntry() = android.content.ClipData.newPlainText(this, this).toClipEntry()
