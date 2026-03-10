package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.AnimeState
import org.application.shikiapp.shared.models.ui.Anime
import org.application.shikiapp.shared.models.ui.AnimeT
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.network.response.Response
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

        setCommentParams(data.topicId)

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
                ContentDetailEvent.Media.ShowFansubbers -> updateState { it.copy(showFansubbers = !it.showFansubbers) }
                ContentDetailEvent.Media.ShowFandubbers -> updateState { it.copy(showFandubbers = !it.showFandubbers) }

                ContentDetailEvent.Media.ShowLinks -> updateState {
                    it.copy(
                        showLinks = !it.showLinks,
                        showSheet = !it.showSheet
                    )
                }

                ContentDetailEvent.Media.ShowRate -> updateState { it.copy(showRate = !it.showRate) }

                is ContentDetailEvent.Media.ShowImage -> updateState {
                    it.copy(
                        showScreenshot = !it.showScreenshot,
                        screenshot = event.index
                    )
                }
            }

            is ContentDetailEvent.Media.Anime -> when (event) {
                ContentDetailEvent.Media.Anime.ShowScreenshots -> updateState {
                    it.copy(showScreenshots = !it.showScreenshots)
                }

                ContentDetailEvent.Media.Anime.ShowVideo -> updateState {
                    it.copy(showVideo = !it.showVideo)
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
            }

            else -> Unit
        }
    }
}