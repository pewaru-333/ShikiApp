package org.application.shikiapp.shared.network.calls.dark

import androidx.paging.PagingData
import com.apollographql.apollo.ApolloClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.application.shikiapp.generated.darkshiki.AnimeExtraQuery
import org.application.shikiapp.generated.shikiapp.AnimeMainQuery
import org.application.shikiapp.shared.models.data.AnimeBasic
import org.application.shikiapp.shared.models.data.Franchise
import org.application.shikiapp.shared.models.ui.AnimeT
import org.application.shikiapp.shared.models.ui.Comment
import org.application.shikiapp.shared.models.ui.Review
import org.application.shikiapp.shared.models.ui.mappers.dark.AnimeMapper
import org.application.shikiapp.shared.network.calls.repository.AnimeRepository
import org.application.shikiapp.shared.utils.extensions.cachedFlow

class IAnimeRepository(private val apollo: ApolloClient) : AnimeRepository {
    override fun getAnimeRawData(id: String) = combine(
        transform = ::DarkShikiAnimeT,
        flow = apollo.cachedFlow(
            query = AnimeMainQuery(id),
            mapData = { data -> data.animes.first() }
        ),
        flow2 = apollo.cachedFlow(
            query = AnimeExtraQuery(id),
            mapData = { data -> data.animes.first() }
        )
    )
}

class DarkShikiAnimeT(
    val main: AnimeMainQuery.Data.Anime,
    val extra: AnimeExtraQuery.Data.Anime
) : AnimeT {
    override val url: String get() = main.url
    override val topicId: Long? get() = main.topic?.id?.toLong()

    override fun mapToAnime(
        franchise: Franchise,
        similar: List<AnimeBasic>,
        favoured: Boolean,
        comments: Flow<PagingData<Comment>>,
        reviews: Flow<PagingData<Review>>
    ) = AnimeMapper.create(
        main = main,
        extra = extra,
        franchise = franchise,
        similar = similar,
        comments = comments,
        reviews = reviews,
        favoured = favoured
    )
}