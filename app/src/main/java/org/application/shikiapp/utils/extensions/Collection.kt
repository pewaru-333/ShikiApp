package org.application.shikiapp.utils.extensions

fun <T> Collection<T>.commaJoin() = joinToString(",")

fun <E> MutableCollection<E>.toggle(element: E) = if (element in this) remove(element)
else add(element)
