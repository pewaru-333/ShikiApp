package org.application.shikiapp.models.views

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.CatalogItems

class CatalogViewModel : ViewModel() {
    private val _state = MutableStateFlow(CatalogState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<DrawerEvent>()
    val event = _event.asSharedFlow()

    fun setSearch(text: String) = _state.update { it.copy(search = text) }

    fun showFilters(menu: CatalogItems) = _state.update {
        when (menu) {
            CatalogItems.Anime -> it.copy(showFiltersA = true)
            CatalogItems.Manga -> it.copy(showFiltersM = true)
            CatalogItems.People -> it.copy(showFiltersP = true)
            else -> CatalogState()
        }
    }

    fun hideFilters() = _state.update {
        it.copy(showFiltersA = false, showFiltersM = false, showFiltersP = false)
    }

    fun drawer() {
        viewModelScope.launch { _event.emit(DrawerEvent.Click) }
    }

    fun pick(menu: CatalogItems) {
        viewModelScope.launch {
            _event.emit(DrawerEvent.Clear)
            _state.update {
                it.copy(menu = menu, search = BLANK, drawerState = DrawerState(DrawerValue.Closed))
            }
        }
    }

    sealed interface DrawerEvent {
        data object Clear : DrawerEvent
        data object Click : DrawerEvent
    }
}

data class CatalogState(
    val menu: CatalogItems = CatalogItems.Anime,
    val search: String = BLANK,
    val showFiltersA: Boolean = false,
    val showFiltersM: Boolean = false,
    val showFiltersP: Boolean = false,
    val listA: LazyListState = LazyListState(),
    val listM: LazyListState = LazyListState(),
    val listC: LazyListState = LazyListState(),
    val listP: LazyListState = LazyListState(),
    val drawerState: DrawerState = DrawerState(DrawerValue.Closed)
)