package org.application.shikiapp.shared.utils.extensions

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle

actual fun Double.format(fractionDigits: Int): String {
    val formatter = NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterDecimalStyle
        minimumFractionDigits = fractionDigits.toULong()
        maximumFractionDigits = fractionDigits.toULong()
    }

    return formatter.stringFromNumber(NSNumber(this)) ?: toString()
}