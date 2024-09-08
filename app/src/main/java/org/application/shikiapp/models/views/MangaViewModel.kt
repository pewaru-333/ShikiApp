@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.views

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.MangaQuery.Data.Manga
import org.application.shikiapp.models.data.ExternalLink
import org.application.shikiapp.models.data.MangaShort
import org.application.shikiapp.network.ApolloClient
import org.application.shikiapp.network.NetworkClient
import org.application.shikiapp.utils.LINKED_TYPE

class MangaViewModel(private val mangaId: String) : ViewModel() {
    private val _response = MutableStateFlow<Response>(Response.Loading)
    val response = _response.asStateFlow()

    private val _state = MutableStateFlow(MangaState())
    val state = _state.asStateFlow()

    init {
        getManga()
    }

    fun getManga() {
        viewModelScope.launch {
            _response.emit(Response.Loading)

            try {
                val manga = ApolloClient.getManga(mangaId)
                val similar = NetworkClient.manga.getSimilar(mangaId)
                val links = NetworkClient.manga.getLinks(mangaId.toLong())
                val favoured = NetworkClient.manga.getManga(mangaId).favoured

                _response.emit(Response.Success(manga, similar, links, favoured))
            } catch (e: Throwable) {
                _response.emit(Response.Error)
            }
        }
    }

    fun changeFavourite(flag: Boolean) {
        viewModelScope.launch {
            try {
                if (flag) NetworkClient.profile.deleteFavourite(LINKED_TYPE[1], mangaId.toLong())
                else NetworkClient.profile.addFavourite(LINKED_TYPE[1], mangaId.toLong())
                getManga()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun reload() {
        viewModelScope.launch { hideRate(); delay(300); getManga() }
    }

    fun showComments() = _state.update { it.copy(showComments = true) }
    fun hideComments() = _state.update { it.copy(showComments = false) }

    fun showSheet() = _state.update { it.copy(showSheet = true) }
    fun hideSheet() = _state.update { it.copy(showSheet = false) }

    fun showRelated() = _state.update { it.copy(showRelated = true) }
    fun hideRelated() = _state.update { it.copy(showRelated = false) }

    fun showCharacters() = _state.update { it.copy(showCharacters = true) }
    fun hideCharacters() = _state.update { it.copy(showCharacters = false) }

    fun showAuthors() = _state.update { it.copy(showAuthors = true) }
    fun hideAuthors() = _state.update { it.copy(showAuthors = false) }

    fun showRate() = _state.update { it.copy(showRate = true, showSheet = false) }
    fun hideRate() = _state.update { it.copy(showRate = false) }

    fun showSimilar() = _state.update { it.copy(showSimilar = true, showSheet = false) }
    fun hideSimilar() = _state.update { it.copy(showSimilar = false, showSheet = true) }

    fun showStats() = _state.update { it.copy(showStats = true, showSheet = false) }
    fun hideStats() = _state.update { it.copy(showStats = false, showSheet = true) }

    fun showLinks() = _state.update { it.copy(showSheet = false, showLinks = true) }
    fun hideLinks() = _state.update { it.copy(showSheet = true, showLinks = false) }


    sealed interface Response {
        data object Error : Response
        data object Loading : Response
        data class Success(
            val manga: Manga,
            val similar: List<MangaShort>,
            val links: List<ExternalLink>,
            val favoured: Boolean
        ) : Response
    }
}

data class MangaState(
    val showComments: Boolean = false,
    val showSheet: Boolean = false,
    val showRelated: Boolean = false,
    val showCharacters: Boolean = false,
    val showAuthors: Boolean = false,
    val showRate: Boolean = false,
    val showSimilar: Boolean = false,
    val showStats: Boolean = false,
    val showLinks: Boolean = false,
    val sheetBottom: SheetState = SheetState(false, Density(1f)),
    val sheetLinks: SheetState = SheetState(false, Density(1f)),
    val lazyCharacters: LazyListState = LazyListState(),
    val lazyAuthors: LazyListState = LazyListState(),
    val lazySimilar: LazyListState = LazyListState()
)