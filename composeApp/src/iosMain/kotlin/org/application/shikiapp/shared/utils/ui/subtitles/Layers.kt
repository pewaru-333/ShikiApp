package org.application.shikiapp.shared.utils.ui.subtitles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ComposeSubtitleLayer(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Black.copy(alpha = 0.5f),
    textStyle: TextStyle = TextStyle(
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
    )
) {
    if (text.isNotEmpty()) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp, 8.dp)
        ) {
            BasicText(
                text = text,
                style = textStyle,
                modifier = Modifier
                    .background(backgroundColor, RoundedCornerShape(4.dp))
                    .padding(8.dp, 4.dp)
            )
        }
    }
}