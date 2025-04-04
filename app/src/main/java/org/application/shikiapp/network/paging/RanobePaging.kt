package org.application.shikiapp.network.paging

import org.application.MangaListQuery.Data.Manga
import org.application.shikiapp.models.viewModels.CatalogFilters
import org.application.shikiapp.network.client.ApolloClient
import org.application.shikiapp.utils.setScore
import org.application.type.MangaKindEnum

class RanobePaging(private val query: CatalogFilters) : MangaPaging(query) {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Manga> = try {
        val page = params.key ?: 1
        val response = ApolloClient.getMangaList(
            page = page,
            limit = params.loadSize,
            order = query.order,
            kind = if (query.kind.isNotEmpty()) query.kind.joinToString(",")
            else listOf(MangaKindEnum.light_novel, MangaKindEnum.novel).joinToString(","),
            status = query.status.joinToString(","),
            season = query.season.joinToString(","),
            score = setScore(query.status, query.score),
            genre = query.genres.joinToString(","),
            search = query.title
        )

        val prevKey = if (page == 1) null else page - 1
        val nextKey = if (response.isEmpty()) null else page + 1

        LoadResult.Page(response, prevKey, nextKey)
    } catch (e: Throwable) {
        LoadResult.Error(e)
    }
}