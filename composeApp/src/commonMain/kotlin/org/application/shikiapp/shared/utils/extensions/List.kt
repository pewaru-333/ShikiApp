package org.application.shikiapp.shared.utils.extensions

import kotlin.random.Random

fun <T> List<T>.getRandomTrending(): List<T> {
    val count = minOf(size, 8)
    if (count == 0) return emptyList()
    if (size == count) return this

    val newList = ArrayList<T>(count)

    var needed = count
    var left = size

    for (item in this) {
        if (Random.nextInt(left) < needed) {
            newList.add(item)

            needed--
            if (needed == 0) break
        }

        left--
    }

    return newList
}