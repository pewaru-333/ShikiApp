package org.application.shikiapp.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.application.shikiapp.models.data.HistoryAnime
import org.application.shikiapp.network.NetworkClient

class UserHistoryPaging(private val userId: Long) : PagingSource<Int, HistoryAnime>() {

    override fun getRefreshKey(state: PagingState<Int, HistoryAnime>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HistoryAnime> = try {
        val page = params.key ?: 1
        val response = NetworkClient.user.getHistory(userId, page, params.loadSize)

        val prevKey = if (page > 0) page.minus(1) else null
        val nextKey = if (response.isNotEmpty()) page.plus(1) else null

        LoadResult.Page(response, prevKey, nextKey)
    } catch (e: Throwable) {
        LoadResult.Error(e)
    }
}