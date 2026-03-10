package org.application.shikiapp.shared.utils.extensions

import io.ktor.http.URLBuilder
import io.ktor.http.Url
import org.application.shikiapp.shared.utils.BASE_URL

fun String.toFullUri(): Url {
    val fullUri = if (!contains(BASE_URL)) {
        BASE_URL + this
    } else {
        this
    }

    return URLBuilder(fullUri).build()
}