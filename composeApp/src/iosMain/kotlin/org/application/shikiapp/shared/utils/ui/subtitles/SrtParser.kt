package org.application.shikiapp.shared.utils.ui.subtitles

object SrtParser {
    private val TIME_PATTERN = Regex("(\\d{2}):(\\d{2}):(\\d{2}),(\\d{3})")
    private val CUE_TIMING_PATTERN = Regex("(\\d{2}:\\d{2}:\\d{2},\\d{3}) --> (\\d{2}:\\d{2}:\\d{2},\\d{3})")

    fun parse(content: String): SubtitleCueList {
        val lines = content.lines()
        val cues = mutableListOf<SubtitleCue>()
        var i = 0

        while (i < lines.size) {
            if (lines[i].isBlank()) {
                i++
                continue
            }

            val sequenceNumber = lines[i].trim().toIntOrNull()
            if (sequenceNumber != null) {
                i++

                if (i < lines.size) {
                    val timingLine = lines[i]
                    val timingMatch = CUE_TIMING_PATTERN.find(timingLine)

                    if (timingMatch != null) {
                        val startTimeStr = timingMatch.groupValues[1]
                        val endTimeStr = timingMatch.groupValues[2]

                        val startTime = parseTimeToMillis(startTimeStr)
                        val endTime = parseTimeToMillis(endTimeStr)

                        i++
                        val textBuilder = StringBuilder()

                        while (i < lines.size && lines[i].isNotBlank()) {
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
                } else {
                    break
                }
            } else {
                i++
            }
        }

        return SubtitleCueList(cues)
    }

    private fun parseTimeToMillis(timeStr: String): Long {
        val match = TIME_PATTERN.find(timeStr)
        if (match != null) {
            val hours = match.groupValues[1].toLong()
            val minutes = match.groupValues[2].toLong()
            val seconds = match.groupValues[3].toLong()
            val millis = match.groupValues[4].toLong()

            return (hours * 3600 + minutes * 60 + seconds) * 1000 + millis
        }

        return 0
    }
}