package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.AnimeState
import org.application.shikiapp.shared.models.states.BaseDialogState
import org.application.shikiapp.shared.models.ui.Anime
import org.application.shikiapp.shared.models.ui.AnimeT
import org.application.shikiapp.shared.models.ui.Review
import org.application.shikiapp.shared.network.calls.dark.DarkShikiAnimeT
import org.application.shikiapp.shared.network.calls.shiki.ShikiAnimeT
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.paging.ReviewsPaging
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.network.response.Response
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.enums.CommentableType
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.navigation.Screen

class AnimeViewModel(saved: SavedStateHandle) : CachedDetailViewModel<AnimeT, Anime, AnimeState>() {
    override val contentId = saved.toRoute<Screen.Anime>()
        .id.filter(Char::isDigit)

    private var reviewsFlow: Flow<PagingData<Review>>? = null

    override fun initState() = AnimeState()

    override fun getSourceFlow(id: Any) = Network.animeRepository.getAnimeRawData(id.toString())

    override suspend fun transformData(data: AnimeT): Anime {
        val (franchise, similar, favoured) = coroutineScope {
            val franchise = async { Network.anime.getFranchise(contentId) }
            val similar = async { Network.anime.getSimilar(contentId) }
            val favoured = async { Network.anime.getAnime(contentId).favoured }

            Triple(franchise.await(), similar.await(), favoured.await())
        }

        setCommentParams(data.topicId, CommentableType.ANIME)

        val reviews = reviewsFlow ?: Pager(
            config = PagingConfig(pageSize = 15),
            pagingSourceFactory = {
                ReviewsPaging(
                    animeId = when (data) {
                        is ShikiAnimeT -> data.main.url
                        is DarkShikiAnimeT -> data.main.url
                        else -> BLANK
                    }
                )
            }
        ).flow.cachedIn(viewModelScope).also {
            reviewsFlow = it
        }

        return Network.animeRepository.mapToAnime(
            raw = data,
            franchise = franchise,
            similar = similar,
            favoured = favoured,
            comments = comments,
            reviews = reviews
        )
    }

    override fun onEvent(event: ContentDetailEvent) {
        super.onEvent(event)

        when (event) {
            ContentDetailEvent.Media.ChangeRate -> with(response.value) {
                if (this !is Response.Success) return

                val newData = data.copy(userRate = AsyncData.Loading)

                updateState { it.copy(dialogState = null) }

                tryEmit(Response.Success(newData))
                loadData()
            }

            is ContentDetailEvent.ToggleDialog if event.dialogState is BaseDialogState.Media.Image -> updateState {
                it.copy(
                    screenshot = event.dialogState.index
                )
            }

            is ContentDetailEvent.Media.Anime.ToggleFavourite -> with(response.value) {
                if (this !is Response.Success) return

                val isFavoured = data.favoured.getValue() ?: return
                val newData = data.copy(favoured = AsyncData.Loading)

                tryEmit(Response.Success(newData))
                toggleFavourite(
                    id = contentId,
                    type = LinkedType.ANIME,
                    favoured = isFavoured
                )
            }

            else -> Unit
        }
    }
}