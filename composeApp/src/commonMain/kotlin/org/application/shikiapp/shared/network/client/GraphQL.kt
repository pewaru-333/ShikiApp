package org.application.shikiapp.shared.network.client

import com.apollographql.apollo.api.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.application.shikiapp.generated.shikiapp.AnimeAiringQuery
import org.application.shikiapp.generated.shikiapp.AnimeGenresQuery
import org.application.shikiapp.generated.shikiapp.AnimeListQuery
import org.application.shikiapp.generated.shikiapp.AnimeRandomQuery
import org.application.shikiapp.generated.shikiapp.CharacterListQuery
import org.application.shikiapp.generated.shikiapp.MangaGenresQuery
import org.application.shikiapp.generated.shikiapp.MangaListQuery
import org.application.shikiapp.generated.shikiapp.PeopleQuery
import org.application.shikiapp.generated.shikiapp.UsersQuery
import org.application.shikiapp.generated.shikiapp.type.OrderEnum
import org.application.shikiapp.shared.models.ui.mappers.mapper
import org.application.shikiapp.shared.models.ui.mappers.toContent
import org.application.shikiapp.shared.utils.enums.Order
import org.application.shikiapp.shared.utils.extensions.getRandomTrending
import org.application.shikiapp.shared.utils.extensions.mapToResult
import org.application.shikiapp.shared.utils.ui.Formatter

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

    suspend fun getTrending() = Network.apollo.query(AnimeAiringQuery(Formatter.getOngoingSeason()))
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

    fun getMangaGenres() = Network.apollo.query(MangaGenresQuery()).toFlow()
        .mapToResult(MangaGenresQuery.Data::genres)

// ============================================= Other =============================================

    suspend fun getCharacters(page: Int, limit: Int, search: String?) =
        getList(CharacterListQuery(page, limit, search)) {
            it.characters.map(CharacterListQuery.Data.Character::mapper)
        }

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