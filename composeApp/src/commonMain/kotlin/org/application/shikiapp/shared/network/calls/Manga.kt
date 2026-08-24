package org.application.shikiapp.shared.network.calls

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.application.shikiapp.shared.models.data.Franchise
import org.application.shikiapp.shared.models.data.Manga
import org.application.shikiapp.shared.models.data.MangaBasic

class Manga(private val client: HttpClient) {
    suspend fun getManga(id: Any): Manga = client.get("mangas/$id").body()

    suspend fun getSimilar(id: Any): List<MangaBasic> = client.get("mangas/$id/similar").body()

    suspend fun getFranchise(id: Any): Franchise = client.get("mangas/$id/franchise").body()
}