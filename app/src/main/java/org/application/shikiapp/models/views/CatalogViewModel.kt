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
import org.application.shikiapp.models.states.CatalogState
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
            CatalogItems.ANIME -> it.copy(showFiltersA = true)
            CatalogItems.MANGA -> it.copy(showFiltersM = true)
            CatalogItems.RANOBE -> it.copy(showFiltersR = true)
            CatalogItems.PEOPLE -> it.copy(showFiltersP = true)
            else -> CatalogState()
        }
    }

    fun hideFilters() = _state.update {
        it.copy(showFiltersA = false, showFiltersM = false, showFiltersR = false, showFiltersP = false)
    }

    fun drawer() = viewModelScope.launch { _event.emit(DrawerEvent.Click) }

    fun pick(menu: CatalogItems) = viewModelScope.launch {
        _event.emit(DrawerEvent.Clear)
        _state.update {
            it.copy(menu = menu, search = BLANK, drawerState = DrawerState(DrawerValue.Closed))
        }
    }

    sealed interface DrawerEvent {
        data object Clear : DrawerEvent
        data object Click : DrawerEvent
    }
}