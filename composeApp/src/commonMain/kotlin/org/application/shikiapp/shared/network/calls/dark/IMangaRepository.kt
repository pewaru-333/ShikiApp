package org.application.shikiapp.shared.network.calls.dark

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import androidx.paging.PagingData
import com.apollographql.apollo.ApolloClient
import org.application.shikiapp.shared.models.data.Franchise
import org.application.shikiapp.shared.models.data.MangaBasic
import org.application.shikiapp.shared.models.ui.Comment
import org.application.shikiapp.shared.models.ui.Manga
import org.application.shikiapp.generated.shikiapp.MangaMainQuery
import org.application.shikiapp.generated.darkshiki.MangaExtraQuery
import org.application.shikiapp.shared.models.ui.MangaT
import org.application.shikiapp.shared.models.ui.mappers.dark.MangaMapper
import org.application.shikiapp.shared.network.calls.repository.MangaRepository
import org.application.shikiapp.shared.utils.extensions.cachedQueryFlow

class DarkShikiMangaT(
    val main: MangaMainQuery.Data.Manga,
    val extra: MangaExtraQuery.Data.Manga
) : MangaT {
    override val topicId: Long? get() = main.topic?.id?.toLong()
}

class IMangaRepository(private val apollo: ApolloClient) : MangaRepository {
    override fun getMangaRawData(id: String): Flow<MangaT> = combine(
        transform = ::DarkShikiMangaT,
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
        val darkRaw = raw as DarkShikiMangaT
        return MangaMapper.create(
            main = darkRaw.main,
            extra = darkRaw.extra,
            franchise = franchise,
            similar = similar,
            favoured = favoured,
            comments = comments
        )
    }
}