package org.application.shikiapp.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.application.AnimeListQuery
import org.application.shikiapp.models.views.QueryMap
import org.application.shikiapp.network.ApolloClient
import org.application.shikiapp.utils.STATUSES

class AnimePaging(private val queryMap: QueryMap) : PagingSource<Int, AnimeListQuery.Anime>() {

    override fun getRefreshKey(state: PagingState<Int, AnimeListQuery.Anime>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AnimeListQuery.Anime> {
        return try {
            val page = params.key ?: 1
            val response = ApolloClient.getAnimeList(
                page = page,
                limit = params.loadSize,
                order = queryMap.order,
                kind = queryMap.kind.joinToString(","),
                status = queryMap.status.joinToString(","),
                season = queryMap.season.joinToString(","),
                score = setScore(),
                duration = queryMap.duration.joinToString(","),
                rating = queryMap.rating.joinToString(","),
                genre = queryMap.genre.joinToString(","),
//                studio = queryMap.studio,
//                franchise = queryMap.franchise,
//                censored = queryMap.censored,
//                myList = queryMap.myList,
                search = queryMap.title
            )

            val prevKey = if (page > 1) page.minus(1) else null
            val nextKey = if (response.isNotEmpty()) page.plus(1) else null

            LoadResult.Page(data = response, prevKey = prevKey, nextKey = nextKey)
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }

    private fun setScore() = if (STATUSES.keys.elementAt(0) in queryMap.status) null
    else queryMap.score.toInt()
}