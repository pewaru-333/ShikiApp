package org.application.shikiapp.network.calls

import io.ktor.client.HttpClient
import org.application.shikiapp.models.data.Franchise
import org.application.shikiapp.models.data.Manga
import org.application.shikiapp.models.data.MangaBasic
import org.application.shikiapp.utils.extensions.requestWithCache

class Manga(private val client: HttpClient) {
    suspend fun getManga(id: Any) = client.requestWithCache<Manga>(
        cacheKey = "manga:$id",
        url = "mangas/$id"
    )

    suspend fun getSimilar(id: Any) = client.requestWithCache<List<MangaBasic>>(
        cacheKey = "manga_similar:$id",
        url = "mangas/$id/similar"
    )

    suspend fun getFranchise(id: Any) = client.requestWithCache<Franchise>(
        cacheKey = "manga_franchise:$id",
        url = "mangas/$id/franchise"
    )
}