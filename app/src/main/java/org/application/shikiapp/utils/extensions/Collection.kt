package org.application.shikiapp.utils.extensions

fun <E> MutableCollection<E>.toggle(element: E) = if (element in this) remove(element)
else add(element)
