package org.application.shikiapp.shared.utils.extensions

import kotlin.random.Random

fun <T> List<T>.getRandomTrending(): List<T> {
    val count = minOf(size, 8)
    if (count == 0) return emptyList()
    if (size == count) return this

    val array = IntArray(count)

    var needed = count
    var left = size
    var found = 0

    for (i in indices) {
        if (Random.nextInt(left) < needed) {
            array[found++] = i
            needed--
            if (needed == 0) break
        }

        left--
    }

    return array.map { this[it] }
}