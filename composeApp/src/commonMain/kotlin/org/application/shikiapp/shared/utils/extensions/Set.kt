package org.application.shikiapp.shared.utils.extensions

fun <E> Set<E>.toggle(element: E) = if (element in this) this - element else this + element