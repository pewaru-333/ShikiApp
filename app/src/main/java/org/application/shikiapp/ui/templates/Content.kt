package org.application.shikiapp.ui.templates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Names(russian: String?, english: String?, japanese: String?) =
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        when {
            !russian.isNullOrEmpty() -> {
                Text(
                    text = russian,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Companion.Bold
                    )
                )
                english?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                japanese?.let {
                    Text(
                        text = it,
                        style = (if (english != null) MaterialTheme.typography.bodyMedium
                        else MaterialTheme.typography.titleMedium).copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    )
                }
            }

            !english.isNullOrEmpty() -> {
                Text(
                    text = english,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Companion.Bold
                    )
                )
                japanese?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            !japanese.isNullOrEmpty() -> {
                Text(
                    text = japanese,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Companion.Bold
                    )
                )
            }
        }
    }