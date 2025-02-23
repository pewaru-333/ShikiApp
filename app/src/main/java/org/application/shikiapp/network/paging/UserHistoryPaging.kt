package org.application.shikiapp.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.application.shikiapp.models.data.History
import org.application.shikiapp.network.client.NetworkClient

class UserHistoryPaging(private val userId: Long) : PagingSource<Int, History>() {

    override fun getRefreshKey(state: PagingState<Int, History>) =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, History> = try {
        val page = params.key ?: 1
        val response = NetworkClient.user.getHistory(userId, page, params.loadSize)

        val prevKey = if (page == 1) null else page - 1
        val nextKey = if (response.isEmpty()) null else page + 1

        LoadResult.Page(response, prevKey, nextKey)
    } catch (e: Throwable) {
        LoadResult.Error(e)
    }
}