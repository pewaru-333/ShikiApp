package org.application.shikiapp.shared.utils.extensions

import io.ktor.http.URLBuilder
import io.ktor.http.Url
import org.application.shikiapp.shared.utils.BASE_URL
import org.application.shikiapp.shared.utils.URL_MIRROR

fun String.toFullUri(): Url {
    val fullUri = if (contains(BASE_URL) || contains(URL_MIRROR)) {
        this
    } else {
        BASE_URL + this
    }

    return URLBuilder(fullUri).build()
}