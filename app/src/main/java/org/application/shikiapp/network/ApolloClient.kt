package org.application.shikiapp.network

import org.application.AnimeGenresQuery
import org.application.AnimeListQuery
import org.application.AnimeQuery
import org.application.CharacterListQuery
import org.application.CharacterQuery
import org.application.shikiapp.utils.ORDERS
import org.application.type.OrderEnum

object ApolloClient {

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

    suspend fun getAnimeGenres() =
        NetworkClient.apollo.query(AnimeGenresQuery()).execute().data?.genres ?: emptyList()

    suspend fun getAnime(id: String) =
        NetworkClient.apollo.query(AnimeQuery(id)).execute().data?.animes?.first()!!

    suspend fun getCharacters(page: Int, limit: Int, search: String?) =
        NetworkClient.apollo.query(CharacterListQuery(page, limit, search)).execute()
            .data?.characters ?: emptyList()

    suspend fun getCharacter(id: String) =
        NetworkClient.apollo.query(CharacterQuery(listOf(id))).execute().data?.characters
            ?: emptyList()
}