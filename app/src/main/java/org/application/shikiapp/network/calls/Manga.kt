package org.application.shikiapp.network.calls

import org.application.shikiapp.models.data.ExternalLink
import org.application.shikiapp.models.data.Manga
import org.application.shikiapp.models.data.MangaShort
import retrofit2.http.GET
import retrofit2.http.Path

interface Manga {

    @GET("mangas/{mangaId}")
    suspend fun getManga(@Path("mangaId") mangaId: String): Manga

    @GET("mangas/{mangaId}/similar")
    suspend fun getSimilar(@Path("mangaId") mangaId: String): List<MangaShort>

    @GET("mangas/{mangaId}/external_links")
    suspend fun getLinks(@Path("mangaId") mangaId: Long): List<ExternalLink>
}