package org.application.shikiapp.shared.ui.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun TextLabelVideo(text: String, isSelected: Boolean, onClick: () -> Unit) = Text(
    text = text,
    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
    modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)
        .padding(16.dp, 12.dp)
)