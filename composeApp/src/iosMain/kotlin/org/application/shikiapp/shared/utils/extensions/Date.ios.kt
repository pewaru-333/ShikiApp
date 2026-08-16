package org.application.shikiapp.shared.utils.extensions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import org.application.shikiapp.shared.utils.AppLocale
import org.application.shikiapp.shared.utils.enums.DateStyle
import platform.Foundation.*

actual fun LocalDate.format(pattern: String): String {
    val formatter = NSDateFormatter().apply {
        dateFormat = pattern
        locale = NSLocale(AppLocale.getLocale())
    }

    val components = NSDateComponents().apply {
        year = this@format.year.toLong()
        month = this@format.month.number.toLong()
        day = this@format.day.toLong()
    }

    return NSCalendar.currentCalendar.dateFromComponents(components)
        ?.let { formatter.stringFromDate(it) }
        ?: toString()
}

actual fun LocalDate.format(style: DateStyle): String {
    val formatter = NSDateFormatter().apply {
        locale = NSLocale(AppLocale.getLocale())
        dateStyle = when (style) {
            DateStyle.SHORT -> NSDateFormatterShortStyle
            DateStyle.MEDIUM -> NSDateFormatterMediumStyle
            DateStyle.LONG -> NSDateFormatterLongStyle
            DateStyle.FULL -> NSDateFormatterFullStyle
        }
    }

    val components = NSDateComponents().apply {
        year = this@format.year.toLong()
        month = this@format.month.number.toLong()
        day = this@format.day.toLong()
    }

    return NSCalendar.currentCalendar.dateFromComponents(components)
        ?.let { formatter.stringFromDate(it) }
        ?: toString()
}

actual fun LocalDateTime.format(pattern: String): String {
    val formatter = NSDateFormatter().apply {
        locale = NSLocale(AppLocale.getLocale())
        dateFormat = pattern
    }

    val components = NSDateComponents().apply {
        year = this@format.year.toLong()
        month = this@format.month.number.toLong()
        day = this@format.day.toLong()
        hour = this@format.hour.toLong()
        minute = this@format.minute.toLong()
        second = this@format.second.toLong()
    }

    return NSCalendar.currentCalendar.dateFromComponents(components)
        ?.let { formatter.stringFromDate(it) }
        ?: toString()
}