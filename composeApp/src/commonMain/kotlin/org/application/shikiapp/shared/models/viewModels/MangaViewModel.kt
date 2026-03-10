package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.MangaState
import org.application.shikiapp.shared.models.ui.Manga
import org.application.shikiapp.shared.models.ui.MangaT
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.network.response.Response
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.navigation.Screen

class MangaViewModel(saved: SavedStateHandle) : CachedDetailViewModel<MangaT, Manga, MangaState>() {
    override val contentId = saved.toRoute<Screen.Manga>()
        .id.filter(Char::isDigit)

    override fun initState() = MangaState()

    override fun getSourceFlow(id: Any) = Network.mangaRepository.getMangaRawData(id.toString())
      //  GraphQL.getManga(id.toString())

    override suspend fun transformData(data: MangaT): Manga {
       // val (main, extra) = data

        val (franchise, similar, favoured) = coroutineScope {
            val franchise = async { Network.manga.getFranchise(contentId) }
            val similar = async { Network.manga.getSimilar(contentId) }
            val favoured = async { Network.manga.getManga(contentId).favoured }

            Triple(franchise.await(), similar.await(), favoured.await())
        }

        setCommentParams(data.topicId)

        return Network.mangaRepository.mapToManga(
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
            ContentDetailEvent.ShowComments -> updateState { it.copy(showComments = !it.showComments) }
            ContentDetailEvent.ShowSheet -> updateState { it.copy(showSheet = !it.showSheet) }

            is ContentDetailEvent.Media -> when (event) {
                ContentDetailEvent.Media.ChangeRate -> with(response.value) {
                    if (this !is Response.Success) return

                    val newData = data.copy(userRate = AsyncData.Loading)

                    updateState { it.copy(showRate = !it.showRate) }

                    tryEmit(Response.Success(newData))
                    loadData()
                }

                ContentDetailEvent.Media.ShowPoster -> updateState { it.copy(showPoster = !it.showPoster) }
                ContentDetailEvent.Media.ShowAuthors -> updateState { it.copy(showAuthors = !it.showAuthors) }
                ContentDetailEvent.Media.ShowCharacters -> updateState { it.copy(showCharacters = !it.showCharacters) }
                ContentDetailEvent.Media.ShowRelated -> updateState { it.copy(showRelated = !it.showRelated) }
                ContentDetailEvent.Media.ShowSimilar -> updateState { it.copy(showSimilar = !it.showSimilar) }
                ContentDetailEvent.Media.ShowStats -> updateState { it.copy(showStats = !it.showStats) }

                ContentDetailEvent.Media.ShowLinks -> updateState {
                    it.copy(
                        showLinks = !it.showLinks,
                        showSheet = !it.showSheet
                    )
                }

                ContentDetailEvent.Media.ShowRate -> updateState { it.copy(showRate = !it.showRate) }

                else -> Unit
            }

            is ContentDetailEvent.Media.Manga.ToggleFavourite -> with(response.value) {
                if (this !is Response.Success) return

                val isFavoured = data.favoured.getValue() ?: return
                val newData = data.copy(favoured = AsyncData.Loading)

                tryEmit(Response.Success(newData))
                toggleFavourite(
                    id = contentId,
                    type = event.type?.linkedType ?: LinkedType.MANGA,
                    favoured = isFavoured
                )
            }

            else -> Unit
        }
    }
}