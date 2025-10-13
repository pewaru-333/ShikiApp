package org.application.shikiapp.ui.templates

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import org.application.shikiapp.R
import org.application.shikiapp.models.ui.Comment

@Composable
fun IconComment(comments: LazyPagingItems<Comment>, onEvent: () -> Unit) {
    val enabledIcon = IconButtonDefaults.iconButtonColors().contentColor
    val disabledIcon = IconButtonDefaults.iconButtonColors().disabledContentColor

    val isLoading = comments.loadState.refresh is LoadState.Loading
    val isEnabled = !isLoading && comments.itemCount > 0

    val infiniteTransition = rememberInfiniteTransition()
    val fillPercentage by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    IconButton(onEvent, Modifier, isEnabled) {
        VectorIcon(
            resId = R.drawable.vector_comments,
            tint = when {
                isLoading -> enabledIcon.copy(alpha = fillPercentage)
                !isLoading && isEnabled -> enabledIcon
                else -> disabledIcon
            }
        )
    }
}

@Composable
fun NavigationIcon(onClick: () -> Unit) =
    IconButton(onClick) { VectorIcon(R.drawable.vector_arrow_back) }

@Composable
fun VectorIcon(
    @DrawableRes resId: Int,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) = Icon(
    imageVector = ImageVector.vectorResource(resId),
    contentDescription = null,
    modifier = modifier,
    tint = tint
)