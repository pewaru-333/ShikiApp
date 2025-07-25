package org.application.shikiapp.network.client

import com.apollographql.apollo.api.Query
import org.application.shikiapp.generated.AnimeAiringQuery
import org.application.shikiapp.generated.AnimeGenresQuery
import org.application.shikiapp.generated.AnimeListQuery
import org.application.shikiapp.generated.AnimeQuery
import org.application.shikiapp.generated.AnimeStatsQuery
import org.application.shikiapp.generated.CharacterListQuery
import org.application.shikiapp.generated.CharacterQuery
import org.application.shikiapp.generated.MangaGenresQuery
import org.application.shikiapp.generated.MangaListQuery
import org.application.shikiapp.generated.MangaQuery
import org.application.shikiapp.generated.PeopleQuery
import org.application.shikiapp.generated.UserRatesQuery
import org.application.shikiapp.generated.UsersQuery
import org.application.shikiapp.generated.type.OrderEnum
import org.application.shikiapp.generated.type.UserRateTargetTypeEnum
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.models.ui.mappers.toContent
import org.application.shikiapp.utils.enums.Order
import org.application.shikiapp.utils.extensions.getRandomTrending
import org.application.shikiapp.utils.extensions.mapToResult
import org.application.shikiapp.utils.getOngoingSeason

object GraphQL {

    // ============================================= Anime =============================================
    suspend fun getAnimeList(
        page: Int,
        limit: Int,
        order: String = Order.RANKED.name.lowercase(),
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
        Network.apollo.query(AnimeAiringQuery(getOngoingSeason())).execute()
            .dataOrThrow().animes
            .map(AnimeAiringQuery.Data.Anime::mapper)
            .getRandomTrending()

    suspend fun getAnime(id: String) = Network.apollo.query(AnimeQuery(id)).execute()
        .dataOrThrow().animes.first()

    fun getAnimeGenres() = Network.apollo.query(AnimeGenresQuery()).toFlow()
        .mapToResult(AnimeGenresQuery.Data::genres)

    suspend fun getAnimeStats(id: String) =
        Network.apollo.query(AnimeStatsQuery(id)).execute()
            .dataOrThrow().animes.first()

// ============================================= Manga =============================================

    suspend fun getMangaList(
        page: Int,
        limit: Int,
        order: String = Order.RANKED.name.lowercase(),
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

    suspend fun getManga(id: String) = Network.apollo.query(MangaQuery(id)).execute()
        .dataOrThrow().mangas.first()

    fun getMangaGenres() = Network.apollo.query(MangaGenresQuery()).toFlow()
        .mapToResult(MangaGenresQuery.Data::genres)

// ============================================= Other =============================================

    suspend fun getCharacters(page: Int, limit: Int, search: String?) =
        getList(CharacterListQuery(page, limit, search)) {
            it.characters.map(CharacterListQuery.Data.Character::mapper)
        }

    suspend fun getCharacter(id: String) =
        Network.apollo.query(CharacterQuery(listOf(id))).execute()
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

    suspend fun getUsers(
        page: Int,
        limit: Int,
        search: String?
    ) = getList(
        UsersQuery(
            page = page,
            limit = limit,
            search = search
        )
    ) { it.users.map(UsersQuery.Data.User::toContent) }

    suspend fun getUserRates(
        userId: Long,
        page: Int,
        limit: Int,
        type: UserRateTargetTypeEnum
    ) = getList(
        UserRatesQuery(
            userId = userId.toString(),
            page = page,
            limit = limit,
            targetType = type,
            status = null,
            order = null
        )
    ) {
        it.userRates.map { it.mapper(type) }
    }

    private suspend fun <T : Query.Data, R> getList(query: Query<T>, mapper: (T) -> List<R>) =
        try {
            Network.apollo.query(query).execute().dataOrThrow().let(mapper)
        } catch (_: Exception) {
            emptyList()
        }
}