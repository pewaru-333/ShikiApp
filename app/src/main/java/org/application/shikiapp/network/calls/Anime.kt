package org.application.shikiapp.network.calls

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.application.shikiapp.models.data.Anime
import org.application.shikiapp.models.data.AnimeBasic
import org.application.shikiapp.models.data.ExternalLink

class Anime(private val client: HttpClient) {
    suspend fun getAnime(id: Long) = client.get("animes/$id").body<Anime>()
    suspend fun getSimilar(id: Long) = client.get("animes/$id/similar").body<List<AnimeBasic>>()
    suspend fun getLinks(id: Long) = client.get("animes/$id/external_links").body<List<ExternalLink>>()
}