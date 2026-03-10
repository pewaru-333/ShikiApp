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
import org.application.shikiapp.shared.models.ui.Manga
import org.application.shikiapp.shared.models.ui.MangaT
import org.application.shikiapp.shared.models.ui.mappers.MangaMapper
import org.application.shikiapp.shared.network.calls.repository.MangaRepository
import org.application.shikiapp.shared.utils.extensions.cachedQueryFlow

class ShikiMangaT(
    val main: MangaMainQuery.Data.Manga,
    val extra: MangaExtraQuery.Data.Manga
) : MangaT {
    override val topicId: Long? get() = main.topic?.id?.toLong()
}

class IMangaRepository(private val apollo: ApolloClient) : MangaRepository {
    override fun getMangaRawData(id: String): Flow<MangaT> = combine(
        transform = ::ShikiMangaT,
        flow = apollo.cachedQueryFlow(
            query = MangaMainQuery(id),
            cacheKey = "manga_main:$id",
            mapData = { it.mangas.first() }
        ),
        flow2 = apollo.cachedQueryFlow(
            query = MangaExtraQuery(id),
            cacheKey = "manga_extra:$id",
            mapData = { it.mangas.first() }
        )
    )

    override fun mapToManga(
        raw: MangaT,
        franchise: Franchise,
        similar: List<MangaBasic>,
        favoured: Boolean,
        comments: Flow<PagingData<Comment>>
    ): Manga {
        val shikiRaw = raw as ShikiMangaT
        return MangaMapper.create(
            main = shikiRaw.main,
            extra = shikiRaw.extra,
            franchise = franchise,
            similar = similar,
            favoured = favoured,
            comments = comments
        )
    }
}