package org.application.shikiapp.shared.utils.ui.subtitles

object WebVttParser {
    private const val WEBVTT_HEADER = "WEBVTT"
    private val TIME_PATTERN_WITH_HOURS = Regex("(\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{3})")
    private val TIME_PATTERN_WITHOUT_HOURS = Regex("(\\d{2}):(\\d{2})\\.(\\d{3})")
    private val CUE_TIMING_PATTERN = Regex("(\\d{2}:\\d{2}:\\d{2}\\.\\d{3}|\\d{2}:\\d{2}\\.\\d{3}) --> (\\d{2}:\\d{2}:\\d{2}\\.\\d{3}|\\d{2}:\\d{2}\\.\\d{3})")

    fun parse(content: String): SubtitleCueList {
        if (!content.trim().startsWith(WEBVTT_HEADER)) {
            return SubtitleCueList()
        }

        val lines = content.lines()
        val cues = mutableListOf<SubtitleCue>()
        var i = 0

        while (i < lines.size && !CUE_TIMING_PATTERN.matches(lines[i])) {
            i++
        }

        while (i < lines.size) {
            val timingLine = lines[i]
            val timingMatch = CUE_TIMING_PATTERN.find(timingLine)

            if (timingMatch != null) {
                val startTimeStr = timingMatch.groupValues[1]
                val endTimeStr = timingMatch.groupValues[2]

                val startTime = parseTimeToMillis(startTimeStr)
                val endTime = parseTimeToMillis(endTimeStr)

                i++
                val textBuilder = StringBuilder()

                while (i < lines.size && lines[i].isNotEmpty()) {
                    if (textBuilder.isNotEmpty()) {
                        textBuilder.append("\n")
                    }
                    textBuilder.append(lines[i])
                    i++
                }

                val text = textBuilder.toString().trim()
                if (text.isNotEmpty()) {
                    cues.add(SubtitleCue(startTime, endTime, text))
                }
            } else {
                i++
            }
        }

        return SubtitleCueList(cues)
    }

    private fun parseTimeToMillis(timeStr: String): Long {
        val matchWithHours = TIME_PATTERN_WITH_HOURS.find(timeStr)
        if (matchWithHours != null) {
            val hours = matchWithHours.groupValues[1].toLong()
            val minutes = matchWithHours.groupValues[2].toLong()
            val seconds = matchWithHours.groupValues[3].toLong()
            val millis = matchWithHours.groupValues[4].toLong()

            return (hours * 3600 + minutes * 60 + seconds) * 1000 + millis
        }

        val matchWithoutHours = TIME_PATTERN_WITHOUT_HOURS.find(timeStr)
        if (matchWithoutHours != null) {
            val minutes = matchWithoutHours.groupValues[1].toLong()
            val seconds = matchWithoutHours.groupValues[2].toLong()
            val millis = matchWithoutHours.groupValues[3].toLong()

            return (minutes * 60 + seconds) * 1000 + millis
        }

        return 0
    }
}