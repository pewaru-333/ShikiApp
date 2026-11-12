package org.application.shikiapp.shared.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.application.shikiapp.shared.models.ui.Review
import org.application.shikiapp.shared.network.client.Network
import kotlin.coroutines.cancellation.CancellationException

class ReviewsPaging(private val animeId: String, private val animeUrl: String) : PagingSource<Int, Review>() {
    override suspend fun load(params: LoadParams<Int>) = try {
        val page = params.key ?: 1
        val (reviews, nextPage) = Network.animeRepository.getReviews(animeId, animeUrl, page)

        LoadResult.Page(
            data = reviews,
            prevKey = if (page == 1) null else page - 1,
            nextKey = nextPage
        )
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, Review>) =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
}