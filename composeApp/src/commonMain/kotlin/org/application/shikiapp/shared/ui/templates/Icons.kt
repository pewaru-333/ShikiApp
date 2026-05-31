package org.application.shikiapp.shared.ui.templates

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.vector_arrow_back
import shikiapp.composeapp.generated.resources.vector_comments

@Composable
fun IconComment(onClick: () -> Unit) =
    IconButton(onClick) { VectorIcon(Res.drawable.vector_comments) }

@Composable
fun NavigationIcon(onClick: () -> Unit) =
    IconButton(onClick) { VectorIcon(Res.drawable.vector_arrow_back) }

@Composable
fun IconVideoControl(resId: DrawableResource, modifier: Modifier, modifierI: Modifier, onClick: () -> Unit) {
    val interactionSource = remember(::MutableInteractionSource)
    val isFocused by interactionSource.collectIsFocusedAsState()

    val containerColor = if (isFocused) MaterialTheme.colorScheme.primary
    else Color.White.copy(alpha = 0.2f)

    val contentColor = if (isFocused) MaterialTheme.colorScheme.onPrimary
    else Color.Transparent

    IconButton(
        onClick = onClick,
        modifier = modifier,
        interactionSource = interactionSource,
        colors = IconButtonDefaults.iconButtonColors(containerColor, contentColor),
        content = { VectorIcon(resId, modifierI.fillMaxSize(), Color.White) }
    )
}

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