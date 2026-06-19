package org.application.shikiapp.shared.models.ui.mappers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.application.shikiapp.shared.models.data.Calendar
import org.application.shikiapp.shared.models.ui.AnimeCalendar
import org.application.shikiapp.shared.models.ui.list.Content
import org.application.shikiapp.shared.utils.extensions.format
import kotlin.time.Instant

suspend fun List<Calendar>.toSchedule(): List<AnimeCalendar.Schedule> = withContext(Dispatchers.Default) {
    if (isEmpty()) return@withContext emptyList()

    val timeZone = TimeZone.currentSystemDefault()
    val dates = HashMap<String, LocalDate>()
    val result = ArrayList<AnimeCalendar.Schedule>()

    var currentDay: LocalDate? = null
    var currentList = ArrayList<Content>()

    for (item in this@toSchedule) {
        val date = dates.getOrPut(item.nextEpisodeAt) {
            Instant.parse(item.nextEpisodeAt).toLocalDateTime(timeZone).date
        }

        if (date != currentDay) {
            if (currentDay != null) {
                result.add(
                    AnimeCalendar.Schedule(
                        date = currentDay.format("d MMMM, E"),
                        animes = currentList
                    )
                )
            }

            currentDay = date
            currentList = ArrayList()
        }

        currentList.add(item.anime.toContent())
    }

    if (currentDay != null) {
        result.add(
            AnimeCalendar.Schedule(
                date = currentDay.format("d MMMM, E"),
                animes = currentList
            )
        )
    }

    result
}