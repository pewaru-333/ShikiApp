package org.application.shikiapp.shared.ui.templates

import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.BaseDialogState
import org.application.shikiapp.shared.models.ui.UserRate
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.ui.theme.Icons

@Composable
fun FloatingActionButtonContent(
    userRate: AsyncData<UserRate?>?,
    isFavoured: AsyncData<Boolean>,
    isVisible: Boolean,
    onToggleFavourite: () -> Unit,
    onEvent: (ContentDetailEvent) -> Unit,
) {
    if (Preferences.token != null) {
        if (userRate == null) {
            FloatingActionButton(
                modifier = Modifier.animateFloatingActionButton(isVisible, Alignment.BottomEnd),
                onClick = { if (isFavoured is AsyncData.Success) onToggleFavourite() },
                content = {
                    when (isFavoured) {
                        AsyncData.Loading -> CircularProgressIndicator(Modifier.size(24.dp))
                        is AsyncData.Success -> VectorIcon(
                            imageVector = Icons.Favorite,
                            tint = if (isFavoured.data) Color.Red else LocalContentColor.current
                        )
                    }
                }
            )
        } else {
            FloatingActionButtonMenu(
                expanded = true,
                modifier = Modifier
                    .absoluteOffset(16.dp, 16.dp)
                    .animateFloatingActionButton(isVisible, Alignment.BottomEnd),
                button = {
                    FloatingActionButton(
                        onClick = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Rate)) },
                        content = {
                            when (userRate) {
                                AsyncData.Loading -> CircularProgressIndicator(Modifier.size(24.dp))
                                is AsyncData.Success -> VectorIcon(
                                    imageVector = if (userRate.data == null) Icons.Bookmark
                                    else Icons.Edit
                                )
                            }
                        }
                    )
                },
                content = {
                    SmallFloatingActionButton(
                        onClick = { if (isFavoured is AsyncData.Success) onToggleFavourite() },
                        content = {
                            when (isFavoured) {
                                AsyncData.Loading -> CircularProgressIndicator(Modifier.size(24.dp))
                                is AsyncData.Success -> VectorIcon(
                                    imageVector = Icons.Favorite,
                                    tint = if (isFavoured.data) Color.Red else LocalContentColor.current
                                )
                            }
                        }
                    )
                }
            )
        }
    }
}