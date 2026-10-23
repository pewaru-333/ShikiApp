package org.application.shikiapp.shared.utils.ui.subtitles.parsers

import org.application.shikiapp.shared.utils.ui.subtitles.models.SubtitleCue

internal object ParserASS {
    private val overrideTag = Regex("""\{[^}]*}""")
    private val drawingTag = Regex("""\\p[1-9]\d*""")

    fun parse(content: String): List<SubtitleCue> {
        val cues = ArrayList<SubtitleCue>()
        var events = false
        var fields = listOf("layer", "start", "end", "style", "name", "marginl", "marginr", "marginv", "effect", "text")

        for (rawLine in content.lineSequence()) {
            val line = rawLine.trim()
            if (line.startsWith('[')) {
                events = line.equals("[Events]", ignoreCase = true)
                continue
            }
            if (!events) continue

            if (line.startsWith("Format:", ignoreCase = true)) {
                fields = line.substringAfter(':').split(',').map { it.trim().lowercase() }
                continue
            }
            if (!line.startsWith("Dialogue:", ignoreCase = true)) continue

            val values = line.substringAfter(':').split(',', limit = fields.size)
            if (values.size != fields.size) continue

            val start = values.getOrNull(fields.indexOf("start")) ?: continue
            val end = values.getOrNull(fields.indexOf("end")) ?: continue
            val text = values.getOrNull(fields.indexOf("text")) ?: continue
            val startTime = parseTime(start) ?: continue
            val endTime = parseTime(end) ?: continue
            if (endTime <= startTime || drawingTag.containsMatchIn(text)) continue

            val caption = overrideTag.replace(text, "")
                .replace("\\N", "\n")
                .replace("\\n", "\n")
                .replace("\\h", "\u00A0")
                .trim()

            if (caption.isNotEmpty()) {
                cues.add(SubtitleCue(startTime, endTime, caption))
            }
        }

        return cues
    }

    private fun parseTime(value: String): Long? {
        val parts = value.trim().split(':', limit = 3)
        if (parts.size != 3) return null

        val hours = parts[0].toLongOrNull() ?: return null
        val minutes = parts[1].toLongOrNull() ?: return null
        val seconds = parts[2].toDoubleOrNull() ?: return null
        if (hours < 0 || minutes !in 0..59 || !seconds.isFinite() || seconds < 0 || seconds >= 60) return null

        return (hours * 3600 + minutes * 60) * 1000 + (seconds * 1000).toLong()
    }
}
