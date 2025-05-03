package org.application.shikiapp.utils.extensions

import androidx.compose.ui.text.AnnotatedString

fun AnnotatedString.substringBefore(delimiter: String) = subSequence(0, indexOf(delimiter))

fun AnnotatedString.substringAfter(delimiter: String) = subSequence(indexOf(delimiter) + delimiter.length, length)