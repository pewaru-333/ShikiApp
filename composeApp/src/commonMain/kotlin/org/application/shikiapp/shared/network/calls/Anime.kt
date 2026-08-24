package org.application.shikiapp.shared.network.calls

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.application.shikiapp.shared.models.data.Anime
import org.application.shikiapp.shared.models.data.AnimeBasic
import org.application.shikiapp.shared.models.data.Franchise
import org.application.shikiapp.shared.models.data.Review

class Anime(private val client: HttpClient) {
    suspend fun getAnime(id: String): Anime = client.get("animes/$id").body()

    suspend fun getSimilar(id: String): List<AnimeBasic> = client.get("animes/$id/similar").body()

    suspend fun getFranchise(id: String): Franchise = client.get("animes/$id/franchise").body()

    suspend fun getReviews(id: String, page: Int): Review = client.get("$id/reviews/page/$page.json").body()
}