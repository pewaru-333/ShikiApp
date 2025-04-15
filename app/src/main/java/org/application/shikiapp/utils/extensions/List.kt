package org.application.shikiapp.utils.extensions

import kotlin.random.Random

fun <T> List<T>.getRandomTrending(): List<T> {
    val count = minOf(size, 8)
    val indices = mutableListOf<Int>()

    while (indices.size < count) {
        val randomIndex = Random.nextInt(0, size)

        if (randomIndex !in indices) indices.add(randomIndex)
    }

    indices.sort()

    val result = mutableListOf<T>()
    for (index in indices) {
        result.add(this[index])
    }

    return result
}