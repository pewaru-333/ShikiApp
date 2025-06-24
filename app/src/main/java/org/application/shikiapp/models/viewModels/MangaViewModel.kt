@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.viewModels

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.generated.type.MangaKindEnum.light_novel
import org.application.shikiapp.generated.type.MangaKindEnum.novel
import org.application.shikiapp.models.states.MangaState
import org.application.shikiapp.models.ui.Manga
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.network.client.GraphQL
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.navigation.Screen

class MangaViewModel(saved: SavedStateHandle) : ContentDetailViewModel<Manga, MangaState>() {
    private val mangaId = saved.toRoute<Screen.Manga>().id

    override fun initState() = MangaState()

    override fun loadData() {
        viewModelScope.launch {
            emit(Response.Loading)

            try {
                val manga = asyncLoad { GraphQL.getManga(mangaId) }
                val similar = asyncLoad { Network.manga.getSimilar(mangaId) }
                val links = asyncLoad { Network.manga.getLinks(mangaId.toLong()) }
                val favoured = Network.manga.getManga(mangaId).favoured

                val mangaLoaded = manga.await()
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

    override fun onEvent(event: ContentDetailEvent) {
        when (event) {
            ContentDetailEvent.ShowComments -> updateState { it.copy(showComments = !it.showComments) }
            ContentDetailEvent.ShowSheet -> updateState { it.copy(showSheet = !it.showSheet) }

            is ContentDetailEvent.Media -> when (event) {
                ContentDetailEvent.Media.Reload -> viewModelScope.launch {
                    updateState {
                        it.copy(
                            showRate = !it.showRate,
                            showSheet = !it.showSheet
                        )
                    }
                    loadData()
                }

                ContentDetailEvent.Media.ShowAuthors -> updateState { it.copy(showAuthors = !it.showAuthors) }
                ContentDetailEvent.Media.ShowCharacters -> updateState { it.copy(showCharacters = !it.showCharacters) }
                ContentDetailEvent.Media.ShowRelated -> updateState { it.copy(showRelated = !it.showRelated) }
                ContentDetailEvent.Media.ShowSimilar -> updateState {
                    it.copy(
                        showSimilar = !it.showSimilar,
                        showSheet = !it.showSheet
                    )
                }

                ContentDetailEvent.Media.ShowStats -> updateState {
                    it.copy(
                        showStats = !it.showStats,
                        showSheet = !it.showSheet
                    )
                }

                ContentDetailEvent.Media.ShowLinks -> updateState {
                    it.copy(
                        showLinks = !it.showLinks,
                        showSheet = !it.showSheet
                    )
                }

                ContentDetailEvent.Media.ShowRate -> updateState {
                    it.copy(
                        showRate = !it.showRate,
                        showSheet = !it.showSheet
                    )
                }

                else -> Unit
            }

            is ContentDetailEvent.Media.Manga.ToggleFavourite -> toggleFavourite(
                id = mangaId,
                favoured = event.favoured,
                type = if (event.type in listOf(light_novel, novel)) LinkedType.RANOBE
                else LinkedType.MANGA
            )

            else -> Unit
        }
    }
}