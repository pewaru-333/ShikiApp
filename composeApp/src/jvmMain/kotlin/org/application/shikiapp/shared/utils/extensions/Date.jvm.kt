package org.application.shikiapp.shared.utils.extensions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import org.application.shikiapp.shared.utils.enums.DateStyle
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

actual fun LocalDate.format(pattern: String) = toJavaLocalDate()
    .format(DateTimeFormatter.ofPattern(pattern))

actual fun LocalDate.format(style: DateStyle): String {
    val formatStyle = when (style) {
        DateStyle.SHORT -> FormatStyle.SHORT
        DateStyle.MEDIUM -> FormatStyle.MEDIUM
        DateStyle.LONG -> FormatStyle.LONG
        DateStyle.FULL -> FormatStyle.FULL
    }

    return toJavaLocalDate().format(DateTimeFormatter.ofLocalizedDate(formatStyle))
}

actual fun LocalDateTime.format(pattern: String) = toJavaLocalDateTime()
    .format(DateTimeFormatter.ofPattern(pattern))