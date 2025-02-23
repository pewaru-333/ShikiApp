package org.application.shikiapp.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.application.shikiapp.models.data.ClubImages
import org.application.shikiapp.network.client.NetworkClient

class ClubImagesPaging(private val clubId: Long) : PagingSource<Int, ClubImages>() {

    override fun getRefreshKey(state: PagingState<Int, ClubImages>) =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ClubImages> = try {
        val page = params.key ?: 1
        val response = NetworkClient.clubs.getImages(clubId, page, params.loadSize)

        val prevKey = if (page == 1) null else page - 1
        val nextKey = if (response.isEmpty()) null else page + 1

        LoadResult.Page(response, prevKey, nextKey)
    } catch (e: Throwable) {
        LoadResult.Error(e)
    }
}