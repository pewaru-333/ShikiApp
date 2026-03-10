package org.application.shikiapp.shared.utils.extensions

import androidx.compose.ui.platform.toClipEntry

actual fun String.toClipEntry() = android.content.ClipData.newPlainText(this, this).toClipEntry()