package org.application.shikiapp.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.application.PeopleQuery
import org.application.shikiapp.models.views.PeopleFilters
import org.application.shikiapp.network.ApolloClient

class PeoplePaging(private val filters: PeopleFilters) : PagingSource<Int, PeopleQuery.Person>() {

    override fun getRefreshKey(state: PagingState<Int, PeopleQuery.Person>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PeopleQuery.Person> = try {
        val page = params.key ?: 1
        val response = ApolloClient.getPeople(
            page = page,
            limit = params.loadSize,
            search = filters.search,
            isSeyu = filters.isSeyu,
            isProducer = filters.isProducer,
            isMangaka = filters.isMangaka
        )

        val prevKey = if (page > 1) page.minus(1) else null
        val nextKey = if (response.isNotEmpty()) page.plus(1) else null

        LoadResult.Page(response, prevKey, nextKey)
    } catch (e: Throwable) {
        LoadResult.Error(e)
    }
}