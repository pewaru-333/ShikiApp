package org.application.shikiapp.shared.models.ui.mappers

import org.application.shikiapp.shared.models.ui.EpisodeModel
import org.application.shikiapp.shared.models.ui.VideoVoice
import org.application.shikiapp.shared.network.parser.CollapsTitleDetails
import org.application.shikiapp.shared.network.parser.CvhPlaylistResponse
import org.application.shikiapp.shared.network.parser.CvhSources
import org.application.shikiapp.shared.network.parser.KodikResultItem

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
        hasSubtitles = translation.type == "subtitles",
        hasDubbers = translation.type == "voice",
        episodes = mappedEpisodes,
        lastEpisode = lastEpisode ?: episodesCount ?: 1,
        quality = quality?.trim()?.takeIf { it.isNotBlank() && it != "Неизвестно" }
    )
}

fun CollapsTitleDetails.toVideoVoices(targetSeason: Int): List<VideoVoice> {
    if (seasons.isNullOrEmpty() && !voiceActing.isNullOrEmpty() && iframeUrl != null) {
        return voiceActing.mapIndexed { index, voiceName ->
            val movieEpisode = EpisodeModel(
                number = 1,
                link = iframeUrl,
                audioIndex = index,
                screenshot = poster
            )

            VideoVoice(
                id = index,
                title = voiceName,
                hasSubtitles = !subtitle.isNullOrEmpty(),
                hasDubbers = true,
                episodes = listOf(movieEpisode),
                quality = quality,
                lastEpisode = 1
            )
        }
    }

    val season = seasons?.find { it.season == targetSeason } ?: return emptyList()
    val allVoices = season.episodes.flatMap { it.voiceActing.orEmpty() }.distinct()

    return allVoices.mapIndexedNotNull { index, voiceName ->
        val availableEpisodes = season.episodes.mapNotNull { episode ->
            val trackIndex = episode.voiceActing?.indexOf(voiceName) ?: -1
            if (trackIndex < 0 || episode.iframeUrl == null) return@mapNotNull null

            EpisodeModel(
                number = episode.episode,
                link = episode.iframeUrl,
                audioIndex = trackIndex,
                screenshot = episode.iframePoster
            )
        }

        if (availableEpisodes.isEmpty()) return@mapIndexedNotNull null

        VideoVoice(
            id = index,
            title = voiceName,
            hasDubbers = true,
            hasSubtitles = season.episodes.any { !it.subtitle.isNullOrEmpty() },
            episodes = availableEpisodes,
            quality = quality,
            lastEpisode = availableEpisodes.maxOfOrNull(EpisodeModel::number) ?: 0
        )
    }
}

fun CvhPlaylistResponse.toVideoVoices(targetSeason: Int): List<VideoVoice> {
    val seasonItems = items.filter { it.season == targetSeason }
    if (seasonItems.isEmpty()) return emptyList()

    return seasonItems.groupBy { it.voiceStudio }.entries.mapIndexed { index, (studio, episodes) ->
        val isSubtitles = studio.contains("субтитры", ignoreCase = true) ||
                studio.contains("sub", ignoreCase = true) ||
                episodes.any {
                    it.voiceType?.contains("субтитры", ignoreCase = true) == true ||
                            it.voiceType.isNullOrEmpty() && it.voiceStudio.isEmpty()
                }

        val mappedEpisodes = episodes
            .asSequence()
            .map { EpisodeModel(number = it.episode, link = it.vkId) }
            .distinctBy { it.number }
            .sortedBy { it.number }
            .toList()

        VideoVoice(
            id = index,
            title = studio.ifBlank { "Оригинал" },
            hasDubbers = studio.isNotEmpty(),
            hasSubtitles = isSubtitles,
            episodes = mappedEpisodes,
            quality = "Высокое",
            lastEpisode = mappedEpisodes.lastOrNull()?.number ?: 0
        )
    }
}

fun CvhSources.getQualityMap() = buildMap {
    fun putNotNull(quality: Int, url: String?) {
        if (!url.isNullOrBlank()) put(quality, url)
    }

    putNotNull(2160, mpeg4kUrl)
    putNotNull(1440, mpeg2kUrl) // 2K != QHD?
    putNotNull(1440, mpegQhdUrl) // ????
    putNotNull(1080, mpegFullHdUrl)
    putNotNull(720, mpegHighUrl)
    putNotNull(480, mpegMediumUrl)
    putNotNull(360, mpegLowUrl)
    putNotNull(240, mpegLowestUrl)
    putNotNull(144, mpegTinyUrl)
}