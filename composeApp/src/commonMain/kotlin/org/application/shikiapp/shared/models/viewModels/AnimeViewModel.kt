package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.AnimeState
import org.application.shikiapp.shared.models.states.BaseDialogState
import org.application.shikiapp.shared.models.ui.Anime
import org.application.shikiapp.shared.models.ui.AnimeT
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.network.response.Response
import org.application.shikiapp.shared.utils.enums.CommentableType
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.navigation.Screen

class AnimeViewModel(saved: SavedStateHandle) : CachedDetailViewModel<AnimeT, Anime, AnimeState>() {
    override val contentId = saved.toRoute<Screen.Anime>()
        .id.filter(Char::isDigit)

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

        return Network.animeRepository.mapToAnime(
            raw = data,
            franchise = franchise,
            similar = similar,
            favoured = favoured,
            comments = comments
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