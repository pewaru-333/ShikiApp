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

        val (reviews, nextPage) = withContext(Dispatchers.Default) {
            val parsedReviews = Formatter.parseReviews(response)
            val parsedNextPage = Formatter.parseReviewsNextPage(response.postloader)

            Pair(parsedReviews, parsedNextPage)
        }

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