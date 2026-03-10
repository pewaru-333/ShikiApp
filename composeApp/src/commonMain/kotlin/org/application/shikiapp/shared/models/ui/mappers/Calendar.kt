package org.application.shikiapp.shared.models.ui.mappers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.application.shikiapp.shared.models.data.Calendar
import org.application.shikiapp.shared.models.ui.AnimeCalendar
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

suspend fun List<Calendar>.toSchedule() = withContext(Dispatchers.Default) {
    groupBy { item ->
        OffsetDateTime.parse(item.nextEpisodeAt).toLocalDate()
    }
        .map { (key, value) ->
            AnimeCalendar.Schedule(
                date = key.format(DateTimeFormatter.ofPattern("d MMMM, E")),
                animes = value.map { it.anime.toContent() }
            )
        }
}