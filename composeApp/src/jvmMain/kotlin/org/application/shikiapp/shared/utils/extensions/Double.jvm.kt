package org.application.shikiapp.shared.utils.extensions

import java.text.NumberFormat

actual fun Double.format(fractionDigits: Int): String {
    val formatter = NumberFormat.getNumberInstance().apply {
        minimumFractionDigits = fractionDigits
        maximumFractionDigits = fractionDigits
    }

    return formatter.format(this)
}