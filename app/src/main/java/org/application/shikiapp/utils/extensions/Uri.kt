package org.application.shikiapp.utils.extensions

import androidx.core.net.toUri
import org.application.shikiapp.utils.BASE_URL

fun String.toFullUri() = if (!contains(BASE_URL)) (BASE_URL + this).toUri() else toUri()