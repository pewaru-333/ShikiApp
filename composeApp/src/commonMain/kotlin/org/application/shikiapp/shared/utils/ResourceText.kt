package org.application.shikiapp.shared.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Immutable
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

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as StringResource
            if (resourceId != other.resourceId) return false
            return args.contentDeepEquals(other.args)
        }

        override fun hashCode(): Int {
            var result = resourceId.hashCode()
            result = 31 * result + args.contentDeepHashCode()
            return result
        }
    }

    class PluralStringResource(
        val resourceId: org.jetbrains.compose.resources.PluralStringResource,
        val count: Int,
        vararg val args: Any
    ) : ResourceText {
        override fun compareTo(other: ResourceText) = -1
        override val defaultValue = BLANK

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as PluralStringResource
            if (resourceId != other.resourceId) return false
            if (count != other.count) return false

            return args.contentDeepEquals(other.args)
        }

        override fun hashCode(): Int {
            var result = resourceId.hashCode()
            result = 31 * result + count
            result = 31 * result + args.contentDeepHashCode()

            return result
        }
    }

    @Composable
    fun asComposableString(): String = when (this) {
        is StaticString -> value

        is MultiString -> buildString {
            for (item in value) {
                when (item) {
                    is ResourceText -> append(item.asComposableString())
                    else -> append(item)
                }
            }
        }

        is StringResource -> if (args.isEmpty()) stringResource(resourceId)
        else {
            val argsArray = Array(args.size) { index ->
                when (val arg = args[index]) {
                    is ResourceText -> arg.asComposableString()
                    else -> arg
                }
            }

            stringResource(resourceId, *argsArray)
        }

        is PluralStringResource -> if (args.isEmpty()) pluralStringResource(resourceId, count)
        else {
            val argsArray = Array(args.size) { index ->
                when (val arg = args[index]) {
                    is ResourceText -> arg.asComposableString()
                    else -> arg
                }
            }

            pluralStringResource(resourceId, count, *argsArray)
        }
    }

    suspend fun asString(): String = when (this) {
        is StaticString -> value

        is MultiString -> buildString {
            for (item in value) {
                when (item) {
                    is ResourceText -> append(item.asString())
                    else -> append(item)
                }
            }
        }

        is StringResource -> if (args.isEmpty()) getString(resourceId)
        else {
            val argsArray = Array(args.size) { index ->
                when (val arg = args[index]) {
                    is ResourceText -> arg.asString()
                    else -> arg
                }
            }

            getString(resourceId, *argsArray)
        }

        is PluralStringResource -> if (args.isEmpty()) getPluralString(resourceId, count)
        else {
            val argsArray = Array(args.size) { index ->
                when (val arg = args[index]) {
                    is ResourceText -> arg.asString()
                    else -> arg
                }
            }

            getPluralString(resourceId, count, *argsArray)
        }
    }
}