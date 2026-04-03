package org.application.shikiapp.shared.utils.extensions

import org.application.shikiapp.shared.utils.BASE_URL
import org.application.shikiapp.shared.utils.URL_MIRROR

fun String.toFullUrl(): String {
    val isAbsolute = startsWith("http", ignoreCase = true) ||
            startsWith("//") ||
            startsWith(BASE_URL) ||
            startsWith(URL_MIRROR)

    return if (isAbsolute) {
        this
    } else {
        "${BASE_URL.removeSuffix("/")}/${removePrefix("/")}"
    }
}