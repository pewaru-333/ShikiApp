package org.application.shikiapp.shared.utils.extensions

import org.application.shikiapp.shared.utils.BLANK
import kotlin.enums.enumEntries

inline fun <reified E : Enum<E>> Enum.Companion.safeValueOf(value: String?): E {
    val entries = enumEntries<E>()
    if (value == null) return entries[0]

    for (entry in entries) {
        if (entry.name.equals(value, true)) {
            return entry
        }
    }

    return entries[0]
}

fun Enum<*>.safeEquals(value: String?) = name.equals(value, true)
fun Enum<*>.toValue(): String {
    if (name.isEmpty()) return BLANK

    val chars = name.toCharArray()
    chars[0] = chars[0].uppercaseChar()

    for (i in 1 until chars.size) {
        chars[i] = chars[i].lowercaseChar()
    }

    return chars.concatToString()
}