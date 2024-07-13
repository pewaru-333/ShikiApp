package org.application.shikiapp.models.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.shikiapp.models.data.Club
import org.application.shikiapp.models.data.User
import org.application.shikiapp.network.NetworkClient
import org.application.shikiapp.network.paging.UserFriendsPaging
import org.application.shikiapp.utils.BLANK

class UserViewModel(private val userId: Long) : ViewModel() {
    private val _response = MutableStateFlow<LoadingState>(LoadingState.Loading)
    val response = _response.asStateFlow()

    private val _state = MutableStateFlow(UserState())
    val state = _state.asStateFlow()

    val friends = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { UserFriendsPaging(userId) }
    ).flow.cachedIn(viewModelScope).retryWhen { _, attempt -> attempt <= 5 }

    init {
        viewModelScope.launch {
            _response.emit(LoadingState.Loading)

            try {
                val user = NetworkClient.user.getUser(userId)
                val clubs = NetworkClient.user.getClubs(userId)

                _response.emit(LoadingState.Success(user, clubs))
            } catch (e: Throwable) {
                _response.emit(LoadingState.Error)
            }
        }
    }

    fun setMenu(menu: Int) {
        viewModelScope.launch { _state.update { it.copy(menu = menu, show = true) } }
    }

    fun close() {
        viewModelScope.launch { _state.update { it.copy(show = false) } }
    }

    fun getTitle(): String = when (_state.value.menu) {
        0 -> "Друзья"
        1 -> "Клубы"
        2 -> "Достижения"
        else -> BLANK
    }
}

data class UserState(
    val menu: Int = 0,
    val show: Boolean = false
)

enum class UserMenus(val row: List<String>) {
    FIRST(listOf("Имя", "Друзья")),
    SECOND(listOf("Пол", "Клубы")),
    THIRD(listOf("Возраст", "Достижения"))
}

sealed interface LoadingState {
    data object Error : LoadingState
    data object Loading : LoadingState
    data class Success(val user: User, val clubs: List<Club>) : LoadingState
}