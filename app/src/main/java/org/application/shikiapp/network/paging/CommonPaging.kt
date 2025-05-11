package org.application.shikiapp.network.paging

import androidx.compose.ui.util.fastForEach
import androidx.paging.PagingSource
import androidx.paging.PagingState

class CommonPaging<T : Any>(
    private val getId: (T) -> Any,
    private val request: suspend (page: Int, params: LoadParams<Int>) -> List<T>,
) : PagingSource<Int, T>() {
    private val contentList = hashSetOf<Any>()

    override fun getRefreshKey(state: PagingState<Int, T>) = state.anchorPosition?.let {
        state.closestPageToPosition(it).let { anchorPage ->
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> = try {
        val page = params.key ?: 1
        val response = request(page, params)

        val newData = mutableListOf<T>()

        response.fastForEach { item ->
            if (contentList.add(getId(item))) {
                newData.add(item)
            }
        }

        val prevKey = if (page == 1) null else page - 1
        val nextKey = if (response.isEmpty()) null else page + 1

        LoadResult.Page(newData, prevKey, nextKey)
    } catch (e: Throwable) {
        LoadResult.Error(e)
    }
}