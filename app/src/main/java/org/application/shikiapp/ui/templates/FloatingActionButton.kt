@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.ui.templates

import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.application.shikiapp.R
import org.application.shikiapp.di.Preferences
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.ui.UserRate
import org.application.shikiapp.network.response.AsyncData

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
                            resId = R.drawable.vector_favorite,
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
                    .animateFloatingActionButton(
                        visible = isVisible,
                        alignment = Alignment.BottomEnd
                    ),
                button = {
                    FloatingActionButton(
                        onClick = { onEvent(ContentDetailEvent.Media.ShowRate) },
                        content = {
                            when (userRate) {
                                AsyncData.Loading -> CircularProgressIndicator(Modifier.size(24.dp))
                                is AsyncData.Success -> VectorIcon(
                                    resId = if (userRate.data == null) R.drawable.vector_bookmark
                                    else R.drawable.vector_edit
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
                                    resId = R.drawable.vector_favorite,
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