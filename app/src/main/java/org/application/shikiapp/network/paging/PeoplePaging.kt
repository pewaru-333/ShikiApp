package org.application.shikiapp.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.application.PeopleQuery.Data.Person
import org.application.shikiapp.models.viewModels.CatalogFilters
import org.application.shikiapp.network.client.ApolloClient

class PeoplePaging(private val filters: CatalogFilters) : PagingSource<Int, Person>() {

    override fun getRefreshKey(state: PagingState<Int, Person>) =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Person> = try {
        val page = params.key ?: 1
        val response = ApolloClient.getPeople(
            page = page,
            limit = params.loadSize,
            search = filters.title,
            isSeyu = filters.isSeyu,
            isProducer = filters.isProducer,
            isMangaka = filters.isMangaka
        )

        val prevKey = if (page == 1) null else page - 1
        val nextKey = if (response.isEmpty()) null else page + 1

        LoadResult.Page(response, prevKey, nextKey)
    } catch (e: Throwable) {
        LoadResult.Error(e)
    }
}