package org.application.shikiapp.network.calls

import org.application.shikiapp.models.data.Anime
import org.application.shikiapp.models.data.AnimeShort
import org.application.shikiapp.models.data.ExternalLink
import retrofit2.http.GET
import retrofit2.http.Path

interface Anime {

    @GET("animes/{animeId}")
    suspend fun getAnime(@Path("animeId") animeId: Long): Anime

    @GET("animes/{animeId}/similar")
    suspend fun getSimilar(@Path("animeId") animeId: Long): List<AnimeShort>

    @GET("animes/{animeId}/external_links")
    suspend fun getLinks(@Path("animeId") animeId: Long): List<ExternalLink>
}