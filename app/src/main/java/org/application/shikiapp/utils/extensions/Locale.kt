package org.application.shikiapp.utils.extensions

import java.util.Locale

fun Locale.getDisplayRegionName() = getDisplayName(this).replaceFirstChar(Char::uppercase)