@file:OptIn(ExperimentalComposeUiApi::class)

package org.application.shikiapp.shared.utils.extensions

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry

actual fun String.toClipEntry() = ClipEntry(java.awt.datatransfer.StringSelection(this))