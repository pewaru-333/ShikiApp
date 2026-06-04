package org.application.shikiapp.shared.utils.extensions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.application.shikiapp.shared.utils.enums.DateStyle

expect fun LocalDate.format(pattern: String): String
expect fun LocalDate.format(style: DateStyle): String

expect fun LocalDateTime.format(pattern: String): String