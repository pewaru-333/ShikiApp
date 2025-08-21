package org.application.shikiapp.utils.extensions

fun <E> Set<E>.toggle(element: E) = if (element in this) this - element else this + element