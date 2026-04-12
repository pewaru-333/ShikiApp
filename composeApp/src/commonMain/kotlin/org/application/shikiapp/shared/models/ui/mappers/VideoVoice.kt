package org.application.shikiapp.shared.models.ui.mappers

import org.application.shikiapp.shared.models.ui.EpisodeModel
import org.application.shikiapp.shared.models.ui.VideoVoice
import org.application.shikiapp.shared.network.parser.KodikResultItem
import org.application.shikiapp.shared.utils.VIDEO_WATCH_TYPE
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_unknown

fun KodikResultItem.toVideoVoice(): VideoVoice {
    val mappedEpisodes = mutableListOf<EpisodeModel>()

    seasons?.forEach { (_, season) ->
        season.episodes.forEach { (episodeString, data) ->
            val epNum = episodeString.toIntOrNull() ?: return@forEach

            mappedEpisodes.add(
                EpisodeModel(
                    number = epNum,
                    link = data.link,
                    screenshot = data.screenshots.firstOrNull()
                )
            )
        }
    }

    if (mappedEpisodes.isNotEmpty()) mappedEpisodes.sortBy(EpisodeModel::number)
    else for (i in 1..(episodesCount ?: 1)) {
        mappedEpisodes.add(
            EpisodeModel(
                number = i,
                link = link,
                screenshot = screenshots?.firstOrNull()
            )
        )
    }

    return VideoVoice(
        id = translation.id,
        title = translation.title,
        type = VIDEO_WATCH_TYPE.getOrDefault(translation.type, Res.string.text_unknown),
        isSubtitles = translation.type == "subtitles",
        link = link,
        episodes = mappedEpisodes,
        lastEpisode = lastEpisode ?: episodesCount ?: 1,
        quality = quality?.trim()?.takeIf { it.isNotBlank() && it != "Неизвестно" }
    )
}