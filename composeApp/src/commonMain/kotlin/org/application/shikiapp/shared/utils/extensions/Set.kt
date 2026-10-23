package org.application.shikiapp.shared.utils.extensions

fun <E> Set<E>.toggle(element: E) = if (element in this) this - element else this + element

fun Set<String>.toggleFilter(value: String): Set<String> {
    val inversed = if (value.startsWith("!")) value.drop(1) else "!$value"

    return (this - inversed).toggle(value)
}