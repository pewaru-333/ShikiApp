package org.application.shikiapp.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.application.CharacterListQuery.Data.Character
import org.application.shikiapp.models.views.CatalogFilters
import org.application.shikiapp.network.ApolloClient

class CharactersPaging(private val query: CatalogFilters) : PagingSource<Int, Character>() {

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> = try {
        val page = params.key ?: 1
        val response = ApolloClient.getCharacters(page, params.loadSize, query.title)

        val prevKey = if (page > 1) page.minus(1) else null
        val nextKey = if (response.isNotEmpty()) page.plus(1) else null

        LoadResult.Page(response, prevKey, nextKey)
    } catch (e: Throwable) {
        LoadResult.Error(e)
    }
}