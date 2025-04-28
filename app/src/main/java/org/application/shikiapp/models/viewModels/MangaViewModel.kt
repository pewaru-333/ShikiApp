@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.viewModels

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.events.MangaDetailEvent
import org.application.shikiapp.models.states.MangaState
import org.application.shikiapp.models.ui.Manga
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.network.client.ApolloClient
import org.application.shikiapp.network.client.NetworkClient
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.navigation.Screen
import org.application.type.MangaKindEnum.light_novel
import org.application.type.MangaKindEnum.novel

class MangaViewModel(saved: SavedStateHandle) : ContentDetailViewModel<Manga, MangaState, MangaDetailEvent>() {
    private val mangaId = saved.toRoute<Screen.Manga>().id

    override fun initState() = MangaState()

    override fun loadData() {
        viewModelScope.launch {
            emit(Response.Loading)

            try {
                val manga = asyncLoad { ApolloClient.getManga(mangaId) }
                val mangaLoaded = manga.await()
                val similar = asyncLoad { NetworkClient.manga.getSimilar(mangaId) }
                val links = asyncLoad { NetworkClient.manga.getLinks(mangaId.toLong()) }
                val favoured = NetworkClient.manga.getManga(mangaId).favoured
                val comments = getComments(mangaLoaded.topic?.id?.toLong())

                emit(
                    Response.Success(
                        mangaLoaded.mapper(
                            similar = similar.await(),
                            links = links.await(),
                            comments = comments,
                            favoured = favoured
                        )
                    )
                )
            } catch (e: Throwable) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: MangaDetailEvent) {
        when (event) {
            MangaDetailEvent.Reload -> viewModelScope.launch {
                updateState {
                    it.copy(
                        showRate = !it.showRate,
                        showSheet = !it.showSheet
                    )
                }
                loadData()
            }

            ContentDetailEvent.ShowComments -> updateState { it.copy(showComments = !it.showComments) }
            ContentDetailEvent.ShowSheet -> updateState { it.copy(showSheet = !it.showSheet) }
            ContentDetailEvent.ShowRelated -> updateState { it.copy(showRelated = !it.showRelated) }
            ContentDetailEvent.ShowSimilar -> updateState {
                it.copy(
                    showSimilar = !it.showSimilar,
                    showSheet = !it.showSheet
                )
            }

            MangaDetailEvent.ShowCharacters -> updateState { it.copy(showCharacters = !it.showCharacters) }
            MangaDetailEvent.ShowAuthors -> updateState { it.copy(showAuthors = !it.showAuthors) }
            ContentDetailEvent.ShowRate, MangaDetailEvent.ShowRate -> updateState {
                it.copy(
                    showRate = !it.showRate,
                    showSheet = !it.showSheet
                )
            }
            ContentDetailEvent.ShowLinks -> updateState {
                it.copy(
                    showLinks = !it.showLinks,
                    showSheet = !it.showSheet
                )
            }

            ContentDetailEvent.ShowStats -> updateState {
                it.copy(
                    showStats = !it.showStats,
                    showSheet = !it.showSheet
                )
            }

            is MangaDetailEvent.ToggleFavourite ->
                toggleFavourite(
                    id = mangaId,
                    favoured = event.favoured,
                    type = if (event.type in listOf(light_novel, novel)) LinkedType.RANOBE
                    else LinkedType.MANGA
                )
        }
    }
}