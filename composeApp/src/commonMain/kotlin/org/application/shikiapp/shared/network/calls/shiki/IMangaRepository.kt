package org.application.shikiapp.shared.network.calls.shiki

import androidx.paging.PagingData
import com.apollographql.apollo.ApolloClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.application.shikiapp.generated.shikiapp.MangaExtraQuery
import org.application.shikiapp.generated.shikiapp.MangaMainQuery
import org.application.shikiapp.shared.models.data.Franchise
import org.application.shikiapp.shared.models.data.MangaBasic
import org.application.shikiapp.shared.models.ui.Comment
import org.application.shikiapp.shared.models.ui.MangaT
import org.application.shikiapp.shared.models.ui.mappers.MangaMapper
import org.application.shikiapp.shared.network.calls.repository.MangaRepository
import org.application.shikiapp.shared.utils.extensions.cachedFlow

class ShikiMangaT(
    val main: MangaMainQuery.Data.Manga,
    val extra: MangaExtraQuery.Data.Manga
) : MangaT {
    override val topicId: Long? get() = main.topic?.id?.toLong()
    override fun mapToManga(
        franchise: Franchise,
        similar: List<MangaBasic>,
        favoured: Boolean,
        comments: Flow<PagingData<Comment>>
    ) = MangaMapper.create(
        main = main,
        extra = extra,
        franchise = franchise,
        similar = similar,
        favoured = favoured,
        comments = comments
    )
}

class IMangaRepository(private val apollo: ApolloClient) : MangaRepository {
    override fun getMangaRawData(id: String): Flow<MangaT> = combine(
        transform = ::ShikiMangaT,
        flow = apollo.cachedFlow(
            query = MangaMainQuery(id),
            mapData = { it.mangas.first() }
        ),
        flow2 = apollo.cachedFlow(
            query = MangaExtraQuery(id),
            mapData = { it.mangas.first() }
        )
    )
}