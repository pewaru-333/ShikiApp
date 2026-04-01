package org.application.shikiapp.shared.ui.templates

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.vector_arrow_back
import shikiapp.composeapp.generated.resources.vector_comments

@Composable
fun IconComment(onEvent: () -> Unit) =
    IconButton(onEvent) { VectorIcon(Res.drawable.vector_comments) }

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