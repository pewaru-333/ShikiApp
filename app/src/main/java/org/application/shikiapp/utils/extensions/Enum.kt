package org.application.shikiapp.utils.extensions

inline fun <reified E : Enum<E>> Enum.Companion.safeValueOf(value: String?) =
    enumValues<E>().firstOrNull { it.name.equals(value, true) } ?: enumValues<E>().first()

inline fun <reified E : Enum<E>> E.safeEquals(value: String?) = name.equals(value, true)
inline fun <reified E : Enum<E>> E.toValue() = name.lowercase().replaceFirstChar(Char::uppercaseChar)