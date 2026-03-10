package org.application.shikiapp.shared.network.calls.shiki

import androidx.paging.PagingData
import com.apollographql.apollo.ApolloClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.application.shikiapp.generated.shikiapp.AnimeMainQuery
import org.application.shikiapp.generated.shikiapp.AnimeExtraQuery
import org.application.shikiapp.shared.models.data.AnimeBasic
import org.application.shikiapp.shared.models.ui.Comment
import org.application.shikiapp.shared.models.data.Franchise
import org.application.shikiapp.shared.models.ui.Anime
import org.application.shikiapp.shared.models.ui.AnimeT
import org.application.shikiapp.shared.models.ui.mappers.AnimeMapper
import org.application.shikiapp.shared.network.calls.repository.AnimeRepository
import org.application.shikiapp.shared.utils.extensions.cachedQueryFlow

class IAnimeRepository(private val apollo: ApolloClient) : AnimeRepository {
    override fun getAnimeRawData(id: String) = combine(
        transform = ::ShikiAnimeT,
        flow = apollo.cachedQueryFlow(
            query = AnimeMainQuery(id),
            cacheKey = "anime_main:$id",
            mapData = { data -> data.animes.first() }
        ),
        flow2 = apollo.cachedQueryFlow(
            query = AnimeExtraQuery(id),
            cacheKey = "anime_extra:$id",
            mapData = { data -> data.animes.first() }
        )
    )

    override fun mapToAnime(
        raw: AnimeT,
        franchise: Franchise,
        similar: List<AnimeBasic>,
        favoured: Boolean,
        comments: Flow<PagingData<Comment>>
    ): Anime {
        val shikiRaw = raw as ShikiAnimeT
        return AnimeMapper.create(
            main = shikiRaw.main,
            extra = shikiRaw.extra,
            franchise = franchise,
            similar = similar,
            favoured = favoured,
            comments = comments
        )
    }
}

class ShikiAnimeT(
    val main: AnimeMainQuery.Data.Anime,
    val extra: AnimeExtraQuery.Data.Anime
) : AnimeT {
    override val topicId: Long? get() = main.topic?.id?.toLong()
}