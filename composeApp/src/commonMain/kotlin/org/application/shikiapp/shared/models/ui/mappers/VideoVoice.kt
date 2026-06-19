package org.application.shikiapp.shared.models.ui.mappers

import org.application.shikiapp.shared.models.ui.EpisodeModel
import org.application.shikiapp.shared.models.ui.VideoVoice
import org.application.shikiapp.shared.network.parser.AnimeLibEpisodeDetailResponse
import org.application.shikiapp.shared.network.parser.AnimeLibEpisodesList
import org.application.shikiapp.shared.network.parser.AnimeLibTeamItem
import org.application.shikiapp.shared.network.parser.CollapsTitleDetails
import org.application.shikiapp.shared.network.parser.CvhPlaylistResponse
import org.application.shikiapp.shared.network.parser.CvhSources
import org.application.shikiapp.shared.network.parser.KodikResultItem
import org.application.shikiapp.shared.utils.ResourceText
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.origin_original
import shikiapp.composeapp.generated.resources.text_quality_adaptive

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
        title = ResourceText.StaticString(translation.title),
        hasSubtitles = translation.type == "subtitles",
        hasDubbers = translation.type == "voice",
        episodes = mappedEpisodes,
        lastEpisode = lastEpisode ?: episodesCount ?: 1,
        quality = quality?.trim()
            ?.takeIf { it.isNotBlank() && it != "Неизвестно" }
            ?.let { ResourceText.StaticString(it) }
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
                title = ResourceText.StaticString(voiceName),
                hasSubtitles = !subtitle.isNullOrEmpty(),
                hasDubbers = true,
                episodes = listOf(movieEpisode),
                quality = quality?.let { ResourceText.StaticString(it) },
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
            title = ResourceText.StaticString(voiceName),
            hasDubbers = true,
            hasSubtitles = season.episodes.any { !it.subtitle.isNullOrEmpty() },
            episodes = availableEpisodes,
            quality = quality?.let { ResourceText.StaticString(it) },
            lastEpisode = availableEpisodes.maxOfOrNull(EpisodeModel::number) ?: 0
        )
    }
}

fun CvhPlaylistResponse.toVideoVoices(targetSeason: Int): List<VideoVoice> {
    val seasonItems = items.filter { it.season == targetSeason }
    if (seasonItems.isEmpty()) return emptyList()

    return seasonItems.groupBy { it.voiceStudio }.entries.mapIndexed { index, (studio, episodes) ->
        val mappedEpisodes = episodes
            .map { EpisodeModel(number = it.episode, link = it.vkId) }
            .distinctBy { it.number }
            .sortedBy { it.number }

        VideoVoice(
            id = index,
            title = if (!studio.isNullOrBlank()) ResourceText.StaticString(studio)
            else ResourceText.StringResource(Res.string.origin_original),
            hasDubbers = !studio.isNullOrBlank(),
            hasSubtitles = studio == null,
            episodes = mappedEpisodes,
            quality = ResourceText.StringResource(Res.string.text_quality_adaptive),
            lastEpisode = mappedEpisodes.lastOrNull()?.number ?: 0
        )
    }
}

fun AnimeLibEpisodesList.toVideoVoices(teams: List<AnimeLibTeamItem>): List<VideoVoice> {
    if (data.isEmpty()) return emptyList()

    val mappedEpisodes = data
        .map {
            EpisodeModel(
                number = it.number.toIntOrNull() ?: it.itemNumber,
                link = it.id.toString()
            )
        }
        .distinctBy { it.number }
        .sortedBy { it.number }

    return buildList(maxOf(1, teams.size)) {
        add(
            VideoVoice(
                id = 0,
                title = ResourceText.StaticString("AnimeLib"),
                hasDubbers = true,
                hasSubtitles = false,
                episodes = mappedEpisodes,
                quality = ResourceText.StringResource(Res.string.text_quality_adaptive),
                lastEpisode = mappedEpisodes.lastOrNull()?.number ?: 0
            )
        )

        for (i in 1 until teams.size) {
            add(
                VideoVoice(
                    id = i,
                    title = ResourceText.StaticString(teams[i].name),
                    hasDubbers = true,
                    hasSubtitles = false,
                    episodes = emptyList(),
                    quality = null,
                    lastEpisode = 0
                )
            )
        }
    }
}

fun AnimeLibEpisodeDetailResponse.toEpisodeVoices(episodeNumber: Int) = data.players
    .mapIndexedNotNull { index, player ->
        if (!player.player.equals("Animelib", ignoreCase = true)) {
            return@mapIndexedNotNull null
        }

        val isSubtitles = player.translationType?.label?.contains("Субтитры", ignoreCase = true) == true

        val teamName = player.team?.let { ResourceText.StaticString(it.name) }
            ?: player.translationType?.let { ResourceText.StaticString(it.label) }
            ?: ResourceText.StringResource(Res.string.origin_original)

        val quality = player.video?.quality
            ?.maxOfOrNull { it.quality }
            ?.let {
                ResourceText.StaticString(
                    value = when (it) {
                        2160 -> "4K"
                        1440 -> "2K"
                        1080 -> "Full HD"
                        720 -> "HD"
                        else -> "${it}p"
                    }
                )
            }
            ?: ResourceText.StringResource(Res.string.text_quality_adaptive)

        VideoVoice(
            id = player.team?.id?.toInt() ?: index,
            title = teamName,
            hasDubbers = !isSubtitles,
            hasSubtitles = isSubtitles,
            episodes = emptyList(),
            quality = quality,
            lastEpisode = episodeNumber
        )
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