package org.application.shikiapp.shared.ui.templates

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.vector_arrow_back
import shikiapp.composeapp.generated.resources.vector_comments

@Composable
fun IconComment(onLoadState: () -> Pair<Boolean, Int>, onEvent: () -> Unit) {
    val (isLoading, itemCount) = onLoadState()
    val isEnabled = itemCount > 0

    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    when {
        isLoading -> {
            IconButton(
                onClick = {},
                enabled = false,
                content = {
                    VectorIcon(
                        resId = Res.drawable.vector_comments,
                        tint = LocalContentColor.current.copy(alpha = alpha)
                    )
                }
            )
        }

        isEnabled -> {
            IconButton(onEvent) {
                VectorIcon(Res.drawable.vector_comments)
            }
        }

        else -> {
            IconButton(
                onClick = {},
                enabled = false,
                content = {
                    VectorIcon(
                        resId = Res.drawable.vector_comments,
                        tint = IconButtonDefaults.iconButtonColors().disabledContentColor
                    )
                }
            )
        }
    }
}

@Composable
fun NavigationIcon(onClick: () -> Unit) =
    IconButton(onClick) { VectorIcon(Res.drawable.vector_arrow_back) }

@Composable
fun VectorIcon(
    resId: DrawableResource,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) = Icon(
    painter = painterResource(resId),
    contentDescription = null,
    modifier = modifier,
    tint = tint
)