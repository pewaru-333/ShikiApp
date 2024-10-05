package org.application.shikiapp.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.application.MangaListQuery.Data.Manga
import org.application.shikiapp.models.views.CatalogFilters
import org.application.shikiapp.network.ApolloClient
import org.application.shikiapp.utils.setScore

open class MangaPaging(private val query: CatalogFilters) : PagingSource<Int, Manga>() {

    override fun getRefreshKey(state: PagingState<Int, Manga>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Manga> = try {
        val page = params.key ?: 1
        val response = ApolloClient.getMangaList(
            page = page,
            limit = params.loadSize,
            order = query.order,
            kind = query.kind.joinToString(","),
            status = query.status.joinToString(","),
            season = query.season.joinToString(","),
            score = setScore(query.status, query.score),
            genre = query.genres.joinToString(","),
            search = query.title
        )

        val prevKey = if (page > 1) page.minus(1) else null
        val nextKey = if (response.isNotEmpty()) page.plus(1) else null

        LoadResult.Page(data = response, prevKey = prevKey, nextKey = nextKey)
    } catch (e: Throwable) {
        LoadResult.Error(e)
    }
}