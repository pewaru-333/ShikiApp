@file:OptIn(ExperimentalFoundationStyleApi::class)

package org.application.shikiapp.shared.ui.templates

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.fillWidth
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.LocalMaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ParagraphTitle(text: String, modifier: Modifier = Modifier) = Text(
    text = text,
    modifier = modifier,
    color = MaterialTheme.colorScheme.onSurface,
    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.W500)
)

@Composable
fun TextStickyHeader(text: String) = Text(
    text = text,
    style = MaterialTheme.typography.titleMedium,
    modifier = Modifier.styleable {
        fillWidth()
        background(LocalMaterialTheme.currentValue.colorScheme.tertiaryContainer)
        contentPadding(16.dp, 8.dp)
    }
)