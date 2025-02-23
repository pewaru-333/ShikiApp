package org.application.shikiapp.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.application.shikiapp.models.data.UserBasic
import org.application.shikiapp.network.client.NetworkClient

class ClubMembersPaging(private val clubId: Long) : PagingSource<Int, UserBasic>() {

    override fun getRefreshKey(state: PagingState<Int, UserBasic>) =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserBasic> = try {
        val page = params.key ?: 1
        val response = NetworkClient.clubs.getMembers(clubId, page, params.loadSize)

        val prevKey = if (page == 1) null else page - 1
        val nextKey = if (response.isEmpty()) null else page + 1

        LoadResult.Page(response, prevKey, nextKey)
    } catch (e: Throwable) {
        LoadResult.Error(e)
    }
}