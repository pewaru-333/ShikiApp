package org.application.shikiapp.utils

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource

sealed interface ResourceText {
    data class StaticString(val value: String) : ResourceText

    data class MultiString(val value: List<Any>) : ResourceText

    class StringResource(
        @StringRes val resourceId: Int,
        vararg val args: Any
    ) : ResourceText

    class PluralStringResource(
        @PluralsRes val resourceId: Int,
        val count: Int,
        vararg val args: Any
    ) : ResourceText

    @Composable
    fun asString(): String = when (this) {
        is StaticString -> value

        is MultiString -> value.map { (it as? ResourceText)?.asString() ?: it }.joinToString(" ")

        is StringResource -> stringResource(
            resourceId,
            *args.map { (it as? ResourceText)?.asString() ?: it }.toTypedArray()
        )

        is PluralStringResource -> pluralStringResource(resourceId, count, *args)
    }
}