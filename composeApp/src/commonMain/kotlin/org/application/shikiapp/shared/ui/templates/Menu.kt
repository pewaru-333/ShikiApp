package org.application.shikiapp.shared.ui.templates

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed

@Composable
fun <T> BoxWithConstraintsScope.MenuPlayerItems(
    items: List<T>,
    expanded: Boolean,
    onItemClick: (Int, T) -> Unit,
    itemSelected: (Int, T) -> Boolean,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
    maxHeight: Dp,
    menuTextDefaults: MenuPlayerDefaults.MenuItemText = MenuPlayerDefaults.MenuItemText()
) {
    val scrollState = rememberScrollState()

    val containerColor = Color.Black.copy(alpha = 0.85f)
    val focusedContainerColor = Color.White.copy(alpha = 0.5f)

    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .requiredSize(0.dp)
            .wrapContentSize(Alignment.TopCenter, true)
            .heightIn(max = maxHeight)
            .background(containerColor, MenuDefaults.standaloneGroupShape)
            .clip(MenuDefaults.standaloneGroupShape)
    ) {
        Column(Modifier.verticalScroll(scrollState)) {
            items.fastForEachIndexed { index, item ->
                val interactionSource = remember(::MutableInteractionSource)
                val isFocused by interactionSource.collectIsFocusedAsState()

                DropdownMenuItem(
                    selected = itemSelected(index, item),
                    onClick = { onItemClick(index, item) },
                    modifier = modifier,
                    interactionSource = interactionSource,
                    shapes = MenuDefaults.itemShape(index, items.size),
                    text = {
                        Text(
                            text = itemLabel(item),
                            maxLines = menuTextDefaults.maxLines,
                            overflow = menuTextDefaults.overflow,
                            softWrap = menuTextDefaults.softWrap
                        )
                    },
                    colors = MenuDefaults.selectableItemColors(
                        containerColor = if (isFocused) focusedContainerColor else containerColor,
                        textColor = if (itemSelected(index, item)) Color.Unspecified else Color.White,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}

object MenuPlayerDefaults {
    @Immutable
    data class MenuItemText(
        val maxLines: Int = 1,
        val softWrap: Boolean = false,
        val overflow: TextOverflow = TextOverflow.Ellipsis
    )
}