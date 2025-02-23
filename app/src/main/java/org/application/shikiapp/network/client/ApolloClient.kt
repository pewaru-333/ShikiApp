package org.application.shikiapp.network.client

import org.application.AnimeGenresQuery
import org.application.AnimeListQuery
import org.application.AnimeQuery
import org.application.AnimeStatsQuery
import org.application.CharacterListQuery
import org.application.CharacterQuery
import org.application.MangaGenresQuery
import org.application.MangaListQuery
import org.application.MangaQuery
import org.application.PeopleQuery
import org.application.shikiapp.utils.ORDERS
import org.application.type.OrderEnum

object ApolloClient {

// ============================================= Anime =============================================

    suspend fun getAnimeList(
        page: Int,
        limit: Int,
        order: String = ORDERS.keys.elementAt(2),
        kind: String?,
        status: String?,
        season: String?,
        score: Int?,
        duration: String?,
        rating: String?,
        genre: String?,
        search: String?
    ) = NetworkClient.apollo.query(
        AnimeListQuery(
            page, limit, OrderEnum.safeValueOf(order), kind, status, season, score, duration,
            rating, genre, search
        )
    ).execute().data?.animes ?: emptyList()

    suspend fun getAnime(id: String) =
        NetworkClient.apollo.query(AnimeQuery(id)).execute().dataOrThrow().animes.first()

    suspend fun getAnimeGenres() =
        NetworkClient.apollo.query(AnimeGenresQuery()).execute().data?.genres ?: emptyList()

    suspend fun getAnimeStats(id: String) =
        NetworkClient.apollo.query(AnimeStatsQuery(id)).execute().dataOrThrow().animes.first()

// ============================================= Manga =============================================

    suspend fun getMangaList(
        page: Int,
        limit: Int,
        order: String = ORDERS.keys.elementAt(2),
        kind: String?,
        status: String?,
        season: String?,
        score: Int?,
        genre: String?,
        search: String?
    ) = NetworkClient.apollo.query(
        MangaListQuery(
            page, limit, OrderEnum.safeValueOf(order), kind, status, season, score, genre, search
        )
    ).execute().data?.mangas ?: emptyList()

    suspend fun getManga(id: String) =
        NetworkClient.apollo.query(MangaQuery(id)).execute().dataOrThrow().mangas.first()

    suspend fun getMangaGenres() =
        NetworkClient.apollo.query(MangaGenresQuery()).execute().data?.genres ?: emptyList()

// ============================================= Other =============================================

    suspend fun getCharacters(page: Int, limit: Int, search: String?) =
        NetworkClient.apollo.query(CharacterListQuery(page, limit, search)).execute()
            .data?.characters ?: emptyList()

    suspend fun getCharacter(id: String) =
        NetworkClient.apollo.query(CharacterQuery(listOf(id))).execute()
            .dataOrThrow().characters.first()

    suspend fun getPeople(
        page: Int,
        limit: Int,
        search: String?,
        isSeyu: Boolean?,
        isProducer: Boolean?,
        isMangaka: Boolean?
    ) = NetworkClient.apollo.query(PeopleQuery(page, limit, search, isSeyu, isProducer, isMangaka))
        .execute().data?.people ?: emptyList()
}