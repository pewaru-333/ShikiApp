package org.application.shikiapp.shared.utils.ui.subtitles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.application.shikiapp.shared.models.ui.SubtitleTrack
import org.application.shikiapp.shared.utils.loadSubtitleContent

@Composable
fun ComposeSubtitleLayer(
    currentTimeMs: Long,
    subtitleTrack: SubtitleTrack?,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
    ),
    backgroundColor: Color = Color.Black.copy(alpha = 0.5f),
) {
    var subtitles by remember { mutableStateOf<SubtitleCueList?>(null) }

    LaunchedEffect(subtitleTrack) {
        subtitles = if (subtitleTrack != null) {
            try {
                withContext(Dispatchers.Default) {
                    val content = loadSubtitleContent(subtitleTrack.url)
                    val isSrtByExtension = subtitleTrack.url.endsWith(".srt", ignoreCase = true)

                    val isSrtByContent = content.trim().let {
                        val lines = it.lines()
                        lines.size >= 2 &&
                                lines[0].trim().toIntOrNull() != null &&
                                lines[1].contains("-->") &&
                                lines[1].contains(",")
                    }

                    val isVttByContent = content.trim().startsWith("WEBVTT")

                    if (isSrtByExtension || (isSrtByContent && !isVttByContent)) {
                        SrtParser.parse(content)
                    } else {
                        WebVttParser.parse(content)
                    }
                }
            } catch (_: Exception) {
                SubtitleCueList()
            }
        } else {
            null
        }
    }

    Box(modifier.fillMaxSize(), Alignment.BottomCenter) {
        subtitles?.let { cueList ->
            SubtitleDisplay(
                subtitles = cueList,
                currentTimeMs = currentTimeMs,
                textStyle = textStyle,
                backgroundColor = backgroundColor,
            )
        }
    }
}

@Composable
fun SubtitleDisplay(
    subtitles: SubtitleCueList,
    currentTimeMs: Long,
    modifier: Modifier = Modifier,
    textStyle: TextStyle,
    backgroundColor: Color,
) {
    val activeCues = subtitles.getActiveCues(currentTimeMs)

    if (activeCues.isNotEmpty()) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp)
        ) {
            BasicText(
                style = textStyle,
                text = activeCues.joinToString("\n", transform = SubtitleCue::text),
                modifier = Modifier
                    .background(backgroundColor, RoundedCornerShape(4.dp))
                    .padding(8.dp, 4.dp),
            )
        }
    }
}