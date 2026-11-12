package org.application.shikiapp.shared.network.parser.yandex

internal object YandexMapping {
    fun selectSeasons(seasons: List<YandexSeason>, targetSeason: Int, episodeCount: Int?): List<YandexSeason> {
        if (
            seasons.size == 1 &&
            seasons.first().number == targetSeason &&
            (episodeCount == null || episodeCount == seasons.first().count)
        ) return listOf(seasons.first().copy(firstEpisode = 1))

        if (episodeCount == null || episodeCount <= 0) return emptyList()
        if (seasons.mapTo(HashSet(), YandexSeason::number).size != seasons.size) return emptyList()

        var startIndex = -1
        var endIndex = -1
        for (start in seasons.indices) {
            if (targetSeason == 1 && start != 0) break
            if (seasons[start].number < targetSeason) continue

            var count = 0
            for (index in start..seasons.lastIndex) {
                if (index > start && seasons[index].number != seasons[index - 1].number + 1) {
                    break
                }

                count += seasons[index].count
                if (count == episodeCount) {
                    if (startIndex != -1) return emptyList()
                    startIndex = start
                    endIndex = index
                    break
                }

                if (count > episodeCount) break
            }
        }

        if (startIndex == -1) return emptyList()
        return buildList(endIndex - startIndex + 1) {
            var firstEpisode = 1
            for (index in startIndex..endIndex) {
                val season = seasons[index]
                add(season.copy(firstEpisode = firstEpisode))
                firstEpisode += season.count
            }
        }
    }

    data class YandexSeason(
        val number: Int,
        val contentId: String,
        val count: Int,
        val firstEpisode: Int
    )
}