package org.application.shikiapp.shared.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.application.shikiapp.shared.models.ui.Review
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.utils.ui.Formatter

class ReviewsPaging(private val animeId: String) : PagingSource<Int, Review>() {
    override suspend fun load(params: LoadParams<Int>) = try {
        val page = params.key ?: 1

        val response = Network.anime.getReviews(animeId, page)

        val reviews = withContext(Dispatchers.Default) { Formatter.parseReviews(response) }
        val nextPage = withContext(Dispatchers.Default) { Formatter.parseReviewsNextPage(response.postloader) }

        LoadResult.Page(
            data = reviews,
            prevKey = if (page == 1) null else page - 1,
            nextKey = nextPage
        )
    } catch (e: Exception) {
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, Review>) =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
}