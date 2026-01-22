package org.application.shikiapp.network.client

import com.apollographql.apollo.api.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import org.application.shikiapp.generated.AnimeAiringQuery
import org.application.shikiapp.generated.AnimeExtraQuery
import org.application.shikiapp.generated.AnimeGenresQuery
import org.application.shikiapp.generated.AnimeListQuery
import org.application.shikiapp.generated.AnimeMainQuery
import org.application.shikiapp.generated.AnimeRandomQuery
import org.application.shikiapp.generated.CharacterListQuery
import org.application.shikiapp.generated.CharacterQuery
import org.application.shikiapp.generated.MangaExtraQuery
import org.application.shikiapp.generated.MangaGenresQuery
import org.application.shikiapp.generated.MangaListQuery
import org.application.shikiapp.generated.MangaMainQuery
import org.application.shikiapp.generated.PeopleQuery
import org.application.shikiapp.generated.UsersQuery
import org.application.shikiapp.generated.type.OrderEnum
import org.application.shikiapp.models.ui.mappers.AnimeResponse
import org.application.shikiapp.models.ui.mappers.MangaResponse
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.models.ui.mappers.toContent
import org.application.shikiapp.utils.enums.Order
import org.application.shikiapp.utils.extensions.cachedQueryFlow
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
        studio: String?,
        search: String?
    ) = getList(
        mapper = { it.animes.map(AnimeListQuery.Data.Anime::mapper) },
        query = AnimeListQuery(
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
            studio = studio,
            search = search
        )
    )

    suspend fun getTrending() = Network.apollo.query(AnimeAiringQuery(getOngoingSeason()))
        .execute()
        .dataAssertNoErrors
        .animes
        .map(AnimeAiringQuery.Data.Anime::mapper)
        .getRandomTrending()

    suspend fun getRandom() = Network.apollo.query(AnimeRandomQuery())
        .execute()
        .dataAssertNoErrors
        .animes
        .map(AnimeRandomQuery.Data.Anime::mapper)


    fun getAnime(id: String) = combine(
        transform = ::AnimeResponse,
        flow = Network.apollo.cachedQueryFlow(
            query = AnimeMainQuery(id),
            cacheKey = "anime_main:$id",
            mapData = { data -> data.animes.first() }
        ),
        flow2 = Network.apollo.cachedQueryFlow(
            query = AnimeExtraQuery(id),
            cacheKey = "anime_extra:$id",
            mapData = { data -> data.animes.first() }
        )
    )

    fun getAnimeGenres() = Network.apollo.query(AnimeGenresQuery()).toFlow()
        .mapToResult(AnimeGenresQuery.Data::genres)

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
        publisher: String?,
        search: String?
    ) = getList(
        mapper = { it.mangas.map(MangaListQuery.Data.Manga::mapper) },
        query = MangaListQuery(
            page = page,
            limit = limit,
            order = OrderEnum.safeValueOf(order),
            kind = kind,
            status = status,
            season = season,
            score = score,
            genre = genre,
            publisher = publisher,
            search = search
        )
    )

    fun getManga(id: String) = combine(
        transform = ::MangaResponse,
        flow = Network.apollo.cachedQueryFlow(
            query = MangaMainQuery(id),
            cacheKey = "manga_main:$id",
            mapData = { data -> data.mangas.first() }
        ),
        flow2 = Network.apollo.cachedQueryFlow(
            query = MangaExtraQuery(id),
            cacheKey = "manga_extra:$id",
            mapData = { data -> data.mangas.first() }
        )
    )

    fun getMangaGenres() = Network.apollo.query(MangaGenresQuery()).toFlow()
        .mapToResult(MangaGenresQuery.Data::genres)

// ============================================= Other =============================================

    suspend fun getCharacters(page: Int, limit: Int, search: String?) =
        getList(CharacterListQuery(page, limit, search)) {
            it.characters.map(CharacterListQuery.Data.Character::mapper)
        }

    suspend fun getCharacter(id: String) = Network.apollo.query(CharacterQuery(id))
        .execute()
        .dataAssertNoErrors
        .characters
        .first()

    suspend fun getPeople(
        page: Int,
        limit: Int,
        search: String?,
        isSeyu: Boolean?,
        isProducer: Boolean?,
        isMangaka: Boolean?
    ) = getList(
        mapper = { it.people.map(PeopleQuery.Data.Person::mapper) },
        query = PeopleQuery(
            page = page,
            limit = limit,
            search = search,
            isSeyu = isSeyu,
            isProducer = isProducer,
            isMangaka = isMangaka
        )
    )

    suspend fun getUsers(page: Int, limit: Int, search: String?) = getList(
        mapper = { it.users.map(UsersQuery.Data.User::toContent) },
        query = UsersQuery(
            page = page,
            limit = limit,
            search = search
        )
    )

    private suspend fun <T : Query.Data, R> getList(query: Query<T>, mapper: (T) -> List<R>) =
        try {
            val response = Network.apollo.query(query)
                .execute()
                .dataAssertNoErrors

            withContext(Dispatchers.Default) {
                response.let(mapper)
            }
        } catch (_: Exception) {
            emptyList()
        }
}