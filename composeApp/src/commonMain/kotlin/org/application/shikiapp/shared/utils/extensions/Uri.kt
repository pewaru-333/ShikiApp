package org.application.shikiapp.shared.utils.extensions

import org.application.shikiapp.shared.network.client.ApiRoutes

fun String.toFullUrl() = if (startsWith("http", ignoreCase = true) || startsWith("//")) {
    this
} else {
    "${ApiRoutes.workingBaseUrl.removeSuffix("/")}/${removePrefix("/")}"
}