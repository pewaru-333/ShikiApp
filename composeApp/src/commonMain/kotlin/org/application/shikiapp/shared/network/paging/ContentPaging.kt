package org.application.shikiapp.shared.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlin.coroutines.cancellation.CancellationException

class ContentPaging<T : Any>(private val fetch: suspend (Int) -> List<T>) : PagingSource<Int, T>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> = try {
        val currentPage = params.key ?: 1
        val data = fetch(currentPage)

        val prevKey = if (currentPage == 1) null else currentPage - 1
        val nextKey = if (data.isEmpty()) null else currentPage + 1

        LoadResult.Page(data, prevKey, nextKey)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        val position = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(position) ?: return null

        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }
}