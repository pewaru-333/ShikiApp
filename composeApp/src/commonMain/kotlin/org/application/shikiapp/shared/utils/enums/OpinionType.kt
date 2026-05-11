package org.application.shikiapp.shared.utils.enums

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.review_negative
import shikiapp.composeapp.generated.resources.review_neutral
import shikiapp.composeapp.generated.resources.review_positive
import shikiapp.composeapp.generated.resources.text_unknown

enum class OpinionType(val title: StringResource) {
    POSITIVE(Res.string.review_positive),
    NEUTRAL(Res.string.review_neutral),
    NEGATIVE(Res.string.review_negative),
    UNKNOWN(Res.string.text_unknown);

    @Immutable
    data class ReviewCardColors(
        val containerColor: Color,
        val borderColor: Color,
        val badgeContainerColor: Color,
        val badgeContentColor: Color
    )

    @Composable
    fun getCardColors(): ReviewCardColors {
        val colorScheme = MaterialTheme.colorScheme
        val isDark = colorScheme.surface.luminance() < 0.5f

        val positiveColor = if (isDark) Color(0xFF81C784) else Color(0xFF4CAF50)

        return when (this) {
            POSITIVE -> ReviewCardColors(
                containerColor = positiveColor.copy(alpha = 0.1f),
                borderColor = positiveColor.copy(alpha = 0.5f),
                badgeContainerColor = positiveColor.copy(alpha = 0.2f),
                badgeContentColor = positiveColor
            )

            NEGATIVE -> ReviewCardColors(
                containerColor = colorScheme.errorContainer.copy(alpha = 0.3f),
                borderColor = colorScheme.error.copy(alpha = 0.5f),
                badgeContainerColor = colorScheme.errorContainer,
                badgeContentColor = colorScheme.onErrorContainer
            )

            NEUTRAL -> ReviewCardColors(
                containerColor = colorScheme.secondaryContainer.copy(alpha = 0.3f),
                borderColor = colorScheme.secondary.copy(alpha = 0.5f),
                badgeContainerColor = colorScheme.secondaryContainer,
                badgeContentColor = colorScheme.onSecondaryContainer
            )

            UNKNOWN -> ReviewCardColors(
                containerColor = colorScheme.surfaceVariant.copy(alpha = 0.3f),
                borderColor = colorScheme.outlineVariant.copy(alpha = 0.5f),
                badgeContainerColor = colorScheme.surfaceVariant,
                badgeContentColor = colorScheme.onSurfaceVariant
            )
        }
    }
}