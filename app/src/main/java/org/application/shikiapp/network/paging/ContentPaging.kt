package org.application.shikiapp.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.application.shikiapp.models.states.FiltersState

class ContentPaging<T : Any>(
    private val filters: () -> FiltersState,
    private val fetch: suspend (filters: FiltersState, page: Int, params: LoadParams<Int>) -> List<T>
) : PagingSource<Int, T>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> = try {
        val currentPage = params.key ?: 1
        val currentFilters = filters()

        val data = fetch(currentFilters, currentPage, params)

        val prevKey = if (currentPage == 1) null else currentPage - 1
        val nextKey = if (data.isEmpty()) null else currentPage + 1

        LoadResult.Page(
            data = data,
            prevKey = prevKey,
            nextKey = nextKey
        )
    } catch (e: Exception) {
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, T>) =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
}