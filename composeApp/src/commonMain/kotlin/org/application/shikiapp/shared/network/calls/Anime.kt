package org.application.shikiapp.shared.network.calls

import io.ktor.client.HttpClient
import org.application.shikiapp.shared.models.data.Anime
import org.application.shikiapp.shared.models.data.AnimeBasic
import org.application.shikiapp.shared.models.data.Franchise
import org.application.shikiapp.shared.utils.extensions.requestWithCache

class Anime(private val client: HttpClient) {
    suspend fun getAnime(id: String) = client.requestWithCache<Anime>(
        cacheKey = "anime:$id",
        url = "animes/$id"
    )

    suspend fun getSimilar(id: String) = client.requestWithCache<List<AnimeBasic>>(
        cacheKey = "anime_similar:$id",
        url = "animes/$id/similar"
    )

    suspend fun getFranchise(id: String) = client.requestWithCache<Franchise>(
        cacheKey = "anime_franchise:$id",
        url = "animes/$id/franchise"
    )
}