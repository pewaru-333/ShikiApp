package org.application.shikiapp.utils.extensions

fun <T> List<T>.getRandomTrending(): List<T> {
    val count = minOf(size, 8)
    val indices = listOf(0..<size).flatten().shuffled().take(count)

    return filterIndexed { index, _ -> index in indices }
}