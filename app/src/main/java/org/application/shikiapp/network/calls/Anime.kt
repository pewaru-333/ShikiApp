package org.application.shikiapp.network.calls

import org.application.shikiapp.models.data.Anime
import org.application.shikiapp.models.data.AnimeShort
import org.application.shikiapp.models.data.ExternalLink
import org.application.shikiapp.models.data.Franchise
import org.application.shikiapp.models.data.Related
import org.application.shikiapp.models.data.Role
import org.application.shikiapp.models.data.Screenshot
import retrofit2.http.GET
import retrofit2.http.Path

interface Anime {

    @GET("animes/{animeId}")
    suspend fun getAnime(@Path("animeId") animeId: Long): Anime

    @GET("animes/{animeId}/roles")
    suspend fun getRoles(@Path("animeId") animeId: Long): List<Role>

    @GET("animes/{animeId}/similar")
    suspend fun getSimilar(@Path("animeId") animeId: Long): List<AnimeShort>

    @GET("animes/{animeId}/related")
    suspend fun getRelated(@Path("animeId") animeId: Long): List<Related>

    @GET("animes/{animeId}/screenshots")
    suspend fun getScreenshots(@Path("animeId") animeId: Long): List<Screenshot>

    @GET("animes/{animeId}/franchise")
    suspend fun getFranchise(@Path("animeId") animeId: Long): List<Franchise>

    @GET("animes/{animeId}/external_links")
    suspend fun getLinks(@Path("animeId") animeId: Long): List<ExternalLink>
}