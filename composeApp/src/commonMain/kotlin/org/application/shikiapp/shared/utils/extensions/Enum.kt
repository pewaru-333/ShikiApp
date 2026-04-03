package org.application.shikiapp.shared.utils.extensions

import kotlin.enums.enumEntries

inline fun <reified E : Enum<E>> Enum.Companion.safeValueOf(value: String?) =
    enumEntries<E>().let { entries ->
        entries.firstOrNull { it.name.equals(value, true) } ?: entries.first()
    }

fun Enum<*>.safeEquals(value: String?) = name.equals(value, ignoreCase = true)
inline fun <reified E : Enum<E>> E.toValue() = name.lowercase().replaceFirstChar(Char::uppercaseChar)