package org.application.shikiapp.shared.models.ui.mappers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.application.shikiapp.shared.models.data.Calendar
import org.application.shikiapp.shared.models.ui.AnimeCalendar
import org.application.shikiapp.shared.utils.extensions.format
import kotlin.time.Instant

suspend fun List<Calendar>.toSchedule() = withContext(Dispatchers.Default) {
    val timeZone = TimeZone.currentSystemDefault()

    groupBy { item ->
        Instant.parse(item.nextEpisodeAt).toLocalDateTime(timeZone).date
    }
        .map { (key, value) ->
            AnimeCalendar.Schedule(
                date = key.format("d MMMM, E"),
                animes = value.map { it.anime.toContent() }
            )
        }
}