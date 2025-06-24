package org.application.shikiapp.utils.extensions

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.text.AnnotatedString

fun Int.valueToText(context: Context) = AnnotatedString(context.getString(this))

fun <T> T.valueToText(context: Context, @StringRes id: Int) = AnnotatedString(context.getString(id, this))