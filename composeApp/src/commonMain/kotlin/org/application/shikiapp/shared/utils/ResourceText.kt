package org.application.shikiapp.shared.utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString

sealed interface ResourceText : Comparable<ResourceText> {
    val defaultValue: String

    data class StaticString(val value: String) : ResourceText {
        override fun compareTo(other: ResourceText) = value.compareTo(other.defaultValue)
        override val defaultValue = value
    }

    data class MultiString(val value: List<Any>) : ResourceText {
        override fun compareTo(other: ResourceText) = -1
        override val defaultValue = BLANK
    }

    class StringResource(
        val resourceId: org.jetbrains.compose.resources.StringResource,
        vararg val args: Any
    ) : ResourceText {
        override fun compareTo(other: ResourceText) = -1
        override val defaultValue = BLANK
    }

    class PluralStringResource(
        val resourceId: org.jetbrains.compose.resources.PluralStringResource,
        val count: Int,
        vararg val args: Any
    ) : ResourceText {
        override fun compareTo(other: ResourceText) = -1
        override val defaultValue = BLANK
    }

    @Composable
    fun asComposableString(): String = when (this) {
        is StaticString -> value

        is MultiString -> value.map { (it as? ResourceText)?.asComposableString() ?: it }.joinToString(BLANK)

        is StringResource -> org.jetbrains.compose.resources.stringResource(
            resourceId,
            *args.map { (it as? ResourceText)?.asComposableString() ?: it }.toTypedArray()
        )

        is PluralStringResource -> org.jetbrains.compose.resources.pluralStringResource(resourceId, count, *args)
    }

    suspend fun asString() : String = when (this) {
        is StaticString -> value

        is MultiString -> value.map {
            (it as? ResourceText)?.asString() ?: it
        }.joinToString(BLANK)

        is StringResource -> getString(
            resourceId,
            *args.map { (it as? ResourceText)?.asString() ?: it }.toTypedArray()
        )

        is PluralStringResource -> getPluralString(resourceId, count, *args)
    }
}