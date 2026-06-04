package org.application.shikiapp.shared.utils.extensions

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import platform.Foundation.NSLocale
import platform.Foundation.localizedStringForLocaleIdentifier

actual fun String.getLocaleLocalizedName() = NSLocale(this)
    .localizedStringForLocaleIdentifier(this)
    .replaceFirstChar(Char::uppercase)

@OptIn(ExperimentalComposeUiApi::class)
actual fun String.toClipEntry() = ClipEntry.withPlainText(this)
