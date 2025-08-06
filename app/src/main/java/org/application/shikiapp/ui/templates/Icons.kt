package org.application.shikiapp.ui.templates

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import org.application.shikiapp.R
import org.application.shikiapp.models.data.Comment

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
        Icon(
            painter = painterResource(R.drawable.vector_comments),
            contentDescription = null,
            tint = when {
                isLoading -> enabledIcon.copy(alpha = fillPercentage)
                !isLoading && isEnabled -> enabledIcon
                else -> disabledIcon
            }
        )
    }
}

@Composable
fun NavigationIcon(onClick: () -> Unit) = IconButton(onClick) {
    Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
}