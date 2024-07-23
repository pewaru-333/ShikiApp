package org.application.shikiapp.models.views

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

class CatalogViewModel : ViewModel() {
    private val _state = MutableStateFlow(CatalogState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<DrawerEvent>()
    val event = _event.asSharedFlow()

    fun setSearch(text: String) {
        viewModelScope.launch { _state.update { it.copy(search = text) } }
    }

    fun showFilters(menu: Int) {
        viewModelScope.launch {
            _state.update {
                if (menu == 0) it.copy(showFiltersAnime = true)
                else it.copy(showFiltersPeople = true)
            }
        }
    }

    fun hideFilters() {
        viewModelScope.launch {
            _state.update { it.copy(showFiltersAnime = false, showFiltersPeople = false) }
        }
    }

    fun drawer() {
        viewModelScope.launch { _event.emit(DrawerEvent.ClickDrawer) }
    }

    fun pick(menu: Int) {
        viewModelScope.launch {
            _event.emit(DrawerEvent.ClearDrawer)
            _state.update {
                it.copy(menu = menu, search = BLANK, drawerState = DrawerState(DrawerValue.Closed))
            }
        }
    }

    sealed interface DrawerEvent {
        data object ClearDrawer : DrawerEvent
        data object ClickDrawer : DrawerEvent
    }
}

data class CatalogState(
    val menu: Int = 0,
    val search: String = BLANK,
    val showFiltersAnime: Boolean = false,
    val showFiltersPeople: Boolean = false,
    val drawerState: DrawerState = DrawerState(DrawerValue.Closed)
)