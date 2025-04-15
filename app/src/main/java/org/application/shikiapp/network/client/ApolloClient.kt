package org.application.shikiapp.network.client

import com.apollographql.apollo.api.Query
import org.application.AnimeAiringQuery
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
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.utils.ORDERS
import org.application.shikiapp.utils.extensions.getRandomTrending
import org.application.shikiapp.utils.extensions.mapToResult
import org.application.shikiapp.utils.getOngoingSeason
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
    ) = getList(
        AnimeListQuery(
            page = page,
            limit = limit,
            order = OrderEnum.safeValueOf(order),
            kind = kind,
            status = status,
            season = season,
            score = score,
            duration = duration,
            rating = rating,
            genre = genre,
            search = search
        )
    ) { it.animes.map(AnimeListQuery.Data.Anime::mapper) }


    suspend fun getTrending() =
        NetworkClient.apollo.query(AnimeAiringQuery(getOngoingSeason())).execute()
            .dataOrThrow().animes
            .map(AnimeAiringQuery.Data.Anime::mapper)
            .getRandomTrending()

    suspend fun getAnime(id: String) = NetworkClient.apollo.query(AnimeQuery(id)).execute()
        .dataOrThrow().animes.first()

    fun getAnimeGenres() = NetworkClient.apollo.query(AnimeGenresQuery()).toFlow()
        .mapToResult(AnimeGenresQuery.Data::genres)

    suspend fun getAnimeStats(id: String) =
        NetworkClient.apollo.query(AnimeStatsQuery(id)).execute()
            .dataOrThrow().animes.first()

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
    ) = getList(
        MangaListQuery(
            page = page,
            limit = limit,
            order = OrderEnum.safeValueOf(order),
            kind = kind,
            status = status,
            season = season,
            score = score,
            genre = genre,
            search = search
        )
    ) { it.mangas.map(MangaListQuery.Data.Manga::mapper) }

    suspend fun getManga(id: String) = NetworkClient.apollo.query(MangaQuery(id)).execute()
        .dataOrThrow().mangas.first()

    fun getMangaGenres() = NetworkClient.apollo.query(MangaGenresQuery()).toFlow()
        .mapToResult(MangaGenresQuery.Data::genres)

// ============================================= Other =============================================

    suspend fun getCharacters(page: Int, limit: Int, search: String?) =
        getList(CharacterListQuery(page, limit, search)) {
            it.characters.map(CharacterListQuery.Data.Character::mapper)
        }

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
    ) = getList(
        PeopleQuery(
            page = page,
            limit = limit,
            search = search,
            isSeyu = isSeyu,
            isProducer = isProducer,
            isMangaka = isMangaka
        )
    ) { it.people.map(PeopleQuery.Data.Person::mapper) }

    private suspend fun <T : Query.Data, R> getList(query: Query<T>, mapper: (T) -> List<R>) =
        NetworkClient.apollo.query(query).execute().dataOrThrow().let(mapper)
}