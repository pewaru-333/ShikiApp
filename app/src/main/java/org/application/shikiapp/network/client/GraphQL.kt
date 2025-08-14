package org.application.shikiapp.network.client

import com.apollographql.apollo.api.Query
import org.application.shikiapp.generated.AnimeAiringQuery
import org.application.shikiapp.generated.AnimeExtraQuery
import org.application.shikiapp.generated.AnimeGenresQuery
import org.application.shikiapp.generated.AnimeListQuery
import org.application.shikiapp.generated.AnimeMainQuery
import org.application.shikiapp.generated.AnimeRandomQuery
import org.application.shikiapp.generated.AnimeTopicQuery
import org.application.shikiapp.generated.CharacterListQuery
import org.application.shikiapp.generated.CharacterQuery
import org.application.shikiapp.generated.CharacterTopicQuery
import org.application.shikiapp.generated.MangaExtraQuery
import org.application.shikiapp.generated.MangaGenresQuery
import org.application.shikiapp.generated.MangaListQuery
import org.application.shikiapp.generated.MangaMainQuery
import org.application.shikiapp.generated.MangaTopicQuery
import org.application.shikiapp.generated.PeopleQuery
import org.application.shikiapp.generated.UsersQuery
import org.application.shikiapp.generated.type.OrderEnum
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
        studio: String?,
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
            studio = studio,
            search = search
        )
    ) { it.animes.map(AnimeListQuery.Data.Anime::mapper) }


    suspend fun getTrending() =
        Network.apollo.query(AnimeAiringQuery(getOngoingSeason())).execute()
            .dataOrThrow().animes
            .map(AnimeAiringQuery.Data.Anime::mapper)
            .getRandomTrending()

    suspend fun getRandom() = Network.apollo.query(AnimeRandomQuery()).execute()
        .dataOrThrow().animes
        .map(AnimeRandomQuery.Data.Anime::mapper)

    suspend fun getAnimeMain(id: String) = Network.apollo.query(AnimeMainQuery(id)).execute()
        .dataOrThrow().animes.first()

    suspend fun getAnimeExtra(id: String) = Network.apollo.query(AnimeExtraQuery(id)).execute()
        .dataOrThrow().animes.first()

    suspend fun getAnimeTopic(id: String) = Network.apollo.query(AnimeTopicQuery(id)).execute()
        .dataOrThrow().animes.first()

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
        MangaListQuery(
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
    ) { it.mangas.map(MangaListQuery.Data.Manga::mapper) }

    suspend fun getMangaMain(id: String) = Network.apollo.query(MangaMainQuery(id)).execute()
        .dataOrThrow().mangas.first()

    suspend fun getMangaExtra(id: String) = Network.apollo.query(MangaExtraQuery(id)).execute()
        .dataOrThrow().mangas.first()

    suspend fun getMangaTopic(id: String) = Network.apollo.query(MangaTopicQuery(id)).execute()
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

    suspend fun getCharacterTopic(id: String) = Network.apollo.query(CharacterTopicQuery(listOf(id))).execute()
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

    private suspend fun <T : Query.Data, R> getList(query: Query<T>, mapper: (T) -> List<R>) =
        try {
            Network.apollo.query(query).execute().dataOrThrow().let(mapper)
        } catch (_: Exception) {
            emptyList()
        }
}