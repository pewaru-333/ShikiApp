package org.application.shikiapp.shared.utils.extensions

import java.util.Locale

fun Locale.getDisplayRegionName() = getDisplayName(this).replaceFirstChar(Char::uppercase)