package org.application.shikiapp.ui.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
    modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.tertiaryContainer)
        .padding(16.dp, 8.dp)
)