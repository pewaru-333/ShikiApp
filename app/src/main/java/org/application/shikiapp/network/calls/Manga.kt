package org.application.shikiapp.network.calls

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.application.shikiapp.models.data.ExternalLink
import org.application.shikiapp.models.data.Manga
import org.application.shikiapp.models.data.MangaBasic

class Manga(private val client: HttpClient) {
    suspend fun getManga(id: Any) = client.get("mangas/$id").body<Manga>()
    suspend fun getSimilar(id: Any) = client.get("mangas/$id/similar").body<List<MangaBasic>>()
    suspend fun getLinks(id: Any) = client.get("mangas/$id/external_links").body<List<ExternalLink>>()
}