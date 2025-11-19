@file:OptIn(ExperimentalCoroutinesApi::class)

package org.application.shikiapp.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.application.shikiapp.R
import org.application.shikiapp.di.Preferences
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.data.ClubBasic
import org.application.shikiapp.models.data.FullMessage
import org.application.shikiapp.models.data.MessageToSend
import org.application.shikiapp.models.data.MessageToSendShort
import org.application.shikiapp.models.data.UserBasic
import org.application.shikiapp.models.states.UserDialogState
import org.application.shikiapp.models.states.UserMessagesState
import org.application.shikiapp.models.states.UserState
import org.application.shikiapp.models.ui.History
import org.application.shikiapp.models.ui.User
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.models.ui.list.Dialog
import org.application.shikiapp.models.ui.list.Message
import org.application.shikiapp.models.ui.mappers.mapper
import org.application.shikiapp.models.ui.mappers.toBasicContentMap
import org.application.shikiapp.models.ui.mappers.toDialog
import org.application.shikiapp.models.ui.mappers.toDialogMessage
import org.application.shikiapp.models.ui.mappers.toNewsMessage
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.paging.CommonPaging
import org.application.shikiapp.network.response.AsyncData
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.HtmlParser
import org.application.shikiapp.utils.convertDate
import org.application.shikiapp.utils.enums.FavouriteItem
import org.application.shikiapp.utils.enums.MessageType
import org.application.shikiapp.utils.navigation.Screen

open class UserViewModel(private val saved: SavedStateHandle) : ContentDetailViewModel<User, UserState>() {
    open val userId: Long
        get() = saved.toRoute<Screen.User>().id

    val mailManager by lazy(::MailManager)

    protected val friends = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            CommonPaging(UserBasic::id) { page, params ->
                Network.user.getFriends(userId, page, params.loadSize)
            }
        }
    ).flow
        .cachedIn(viewModelScope)
        .retryWhen { _, attempt -> attempt <= 5 }

    protected val history = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            CommonPaging(History::id) { page, params ->
                Network.user.getHistory(userId, page, params.loadSize)
                    .map(org.application.shikiapp.models.data.History::mapper)
            }
        }
    ).flow
        .cachedIn(viewModelScope)
        .retryWhen { _, attempt -> attempt <= 5 }

    protected val user: Deferred<org.application.shikiapp.models.data.User>
        get() = viewModelScope.async { Network.user.getUser(userId) }

    protected val clubs: Deferred<List<ClubBasic>>
        get() = viewModelScope.async { Network.user.getClubs(userId) }

    protected val favourites: Deferred<Map<FavouriteItem, List<BasicContent>>>
        get() = viewModelScope.async { Network.user.getFavourites(userId).toBasicContentMap() }

    override val contentId = Any()

    override fun initState() = UserState()

    override fun loadData() {
        viewModelScope.launch {
            if (response.value !is Response.Success) {
                emit(Response.Loading)
            }

            try {
                val userLoaded = user.await()

                setCommentParams(userId, "User")

                updateState {
                    it.copy(
                        isFriend = userLoaded.inFriends == true
                    )
                }

                emit(
                    Response.Success(
                        userLoaded.mapper(
                            clubs = clubs.await(),
                            comments = comments,
                            friends = friends,
                            history = history,
                            favourites = favourites.await()
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
            ContentDetailEvent.ShowComments -> updateState {
                it.copy(
                    dialogState = if (it.dialogState == null) UserDialogState.Comments else null
                )
            }

            is ContentDetailEvent.User -> when (event) {
                is ContentDetailEvent.User.ToggleDialog -> {
                    if (event.dialog is UserDialogState.DialogUser) {
                        mailManager.getDialog(event.dialog.userId)
                    }

                    updateState {
                        it.copy(
                            dialogState = if (event.dialog == it.dialogState) null else event.dialog
                        )
                    }
                }

                ContentDetailEvent.User.ToggleFriend -> toggleFriend()

                is ContentDetailEvent.User.PickMenu -> updateState { it.copy(menu = event.menu) }
            }

            else -> Unit
        }
    }

    fun toggleFriend() {
        updateState { it.copy(dialogState = null) }
        viewModelScope.launch {
            try {
                if (state.value.isFriend) Network.user.removeFriend(userId)
                else Network.user.addFriend(userId)
            } catch (_: Throwable) {

            } finally {
                loadData()
            }
        }
    }

    inner class MailManager {
        private val _updatingNotificationMap = MutableStateFlow<Map<Long, NotificationUpdateType>>(emptyMap())

        private val _dialogs = MutableStateFlow<Response<List<Dialog>, Throwable>>(Response.Loading)
        val dialogs = _dialogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), Response.Loading)

        private val _state = MutableStateFlow(UserMessagesState())
        val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UserMessagesState())

        private val _newMessages = MutableStateFlow<List<Dialog>>(emptyList())
        val newMessages = _newMessages.asStateFlow()

        private val _dialogDeleteError = Channel<Int>()
        val dialogDeleteError = _dialogDeleteError.receiveAsFlow()

        val oldMessages = _state.flatMapLatest { state ->
            Pager(
                config = PagingConfig(pageSize = 30, enablePlaceholders = false),
                pagingSourceFactory = {
                    CommonPaging(FullMessage::id) { page, params ->
                        Network.profile.getUserDialog(state.userId, page, params.loadSize)
                    }
                }
            ).flow
                .onStart { _newMessages.emit(emptyList()) }
                .map { list -> list.map(FullMessage::toDialogMessage) }
                .cachedIn(viewModelScope)
                .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
        }

        private var _newsPagingSource: CommonPaging<*>? = null
        val news = Pager(
            config = PagingConfig(pageSize = 15, enablePlaceholders = false),
            pagingSourceFactory = {
                CommonPaging(FullMessage::id) { page, params ->
                    Network.profile.getMessages(Preferences.userId, MessageType.NEWS, page, params.loadSize)
                }.also { _newsPagingSource = it }
            }
        ).flow
            .map { list -> list.map(FullMessage::toNewsMessage) }
            .cachedIn(viewModelScope)
            .combine(_updatingNotificationMap) { pagingData, map ->
                pagingData.map { message ->
                    if (map.contains(message.id)) {
                        when (val data = map.getValue(message.id)) {
                            is NotificationUpdateType.MarkingRead -> message.copy(read = data.state)
                            is NotificationUpdateType.Deleting -> message.copy(isDeleting = data.state)
                        }
                    } else {
                        message
                    }
                }.filter { it.isDeleting.getValue() != true }
            }
            .cachedIn(viewModelScope)

        private var _notificationsPagingSource: CommonPaging<*>? = null
        val notifications = Pager(
            config = PagingConfig(pageSize = 15, enablePlaceholders = false),
            pagingSourceFactory = {
                CommonPaging(FullMessage::id) { page, params ->
                    Network.profile.getMessages(Preferences.userId, MessageType.NOTIFICATIONS, page, params.loadSize)
                }.also { _notificationsPagingSource = it }
            }
        ).flow
            .map { list -> list.map(FullMessage::toNewsMessage) }
            .cachedIn(viewModelScope)
            .combine(_updatingNotificationMap) { pagingData, map ->
                pagingData.map { message ->
                    when (val data = map[message.id]) {
                        is NotificationUpdateType.MarkingRead -> message.copy(read = data.state)
                        is NotificationUpdateType.Deleting -> message.copy(isDeleting = data.state)
                        null -> message
                    }
                }.filter { it.isDeleting.getValue() != true }
            }
            .cachedIn(viewModelScope)

        fun loadData() {
            viewModelScope.launch {
                if (dialogs.value !is Response.Success) {
                   _dialogs.emit(Response.Loading)
                }

                try {
                    val dialogs = Network.profile.getDialogs()
                        .map(org.application.shikiapp.models.data.Dialog::toDialog)

                    _dialogs.emit(Response.Success(dialogs))
                } catch (e: Throwable) {
                    _dialogs.emit(Response.Error(e))
                }
            }
        }

        fun getDialog(userId: Long, nickname: String, avatar: String) {
            updateState { it.copy(dialogState = UserDialogState.DialogUser(userId)) }
            _state.update {
                it.copy(
                    userId = userId,
                    userNickname = AsyncData.Success(nickname),
                    userAvatar = AsyncData.Success(avatar),
                    isFromList = true
                )
            }
        }

        fun getDialog(userId: Long) {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        userId = userId,
                        userNickname = AsyncData.Loading,
                        userAvatar = AsyncData.Loading,
                        isFromList = false
                    )
                }

                try {
                    val (userNickname, userAvatar) = with(Network.user.getUser(userId)) {
                        Pair(nickname, avatar)
                    }

                    _state.update {
                        it.copy(
                            userNickname = AsyncData.Success(userNickname),
                            userAvatar = AsyncData.Success(userAvatar)
                        )
                    }
                } catch (_: Exception) {

                }
            }
        }

        fun sendMessage(message: String) {
            viewModelScope.launch {
                val messageToSend = MessageToSend(
                    frontend = "false",
                    message = MessageToSendShort(
                        body = message,
                        kind = "Private",
                        fromId = Preferences.userId,
                        toId = state.value.userId
                    )
                )

                val tempMessage = Dialog(
                    id = -(_newMessages.value.size - 1).toLong(),
                    userId = Preferences.userId,
                    userNickname = BLANK,
                    userAvatar = BLANK,
                    lastMessage = HtmlParser.parseComment(messageToSend.message.body),
                    lastDate = convertDate(),
                    accountUser = true,
                    isSending = true,
                    isError = false
                )

                _newMessages.update { listOf(tempMessage) + it }

                try {
                    val request = Network.profile.sendMessage(messageToSend)

                    if (request.status != HttpStatusCode.Created) {
                        return@launch
                    } else {
                        _newMessages.update { list ->
                            list.map {
                                if (it.id == tempMessage.id) {
                                    it.copy(isSending = false)
                                } else {
                                    it
                                }
                            }
                        }
                    }
                } catch (_: Exception) {
                    _newMessages.update { list ->
                        list.map {
                            if (it.id == tempMessage.id) {
                                it.copy(isSending = false, isError = true)
                            } else {
                                it
                            }
                        }
                    }
                } finally {
                    loadData()
                }
            }
        }

        fun removeDialog() {
            updateState { it.copy(showDeleteUserDialog = false) }

            viewModelScope.launch {
                try {
                    val request = Network.profile.deleteUserDialog(state.value.userId)

                    if (request.status == HttpStatusCode.OK) {
                        updateState { it.copy(dialogState = UserDialogState.DialogAll) }
                        _dialogDeleteError.send(R.string.text_successfully_deleted_dialog)
                    } else {
                        _dialogDeleteError.send(R.string.text_unsuccessfully_deleted_dialog)
                    }
                } catch (_: Exception) {
                    _dialogDeleteError.send(R.string.text_unsuccessfully_deleted_dialog)
                } finally {
                    loadData()
                }
            }
        }

        fun getUnreadMessages() {
            viewModelScope.launch {
                try {
                    val unread = Network.profile.getUnreadMessages(Preferences.userId)

                    with(unread) {
                        updateState {
                            it.copy(
                                unreadMessages = UserState.UnreadMessages(
                                    messages = messages,
                                    news = news,
                                    notifications = notifications
                                )
                            )
                        }
                    }
                } catch (_: Exception) {

                }
            }
        }

        fun deleteMessage(id: Long) {
            viewModelScope.launch {
                _updatingNotificationMap.update {
                    it + (id to NotificationUpdateType.Deleting(AsyncData.Loading))
                }

                try {
                    val request = Network.profile.deleteMessage(id)

                    if (request.status == HttpStatusCode.NoContent) {
                        _updatingNotificationMap.update {
                            it + (id to NotificationUpdateType.Deleting(AsyncData.Success(true)))
                        }
                    } else {
                        _updatingNotificationMap.update { it - id }
                    }
                } catch (_: Exception) {
                    _updatingNotificationMap.update { it - id }
                }
            }
        }

        fun deleteAllMessages() {
            _state.update { it.copy(showDeleteAll = false) }

            viewModelScope.launch {
                try {
                    val request = Network.profile.deleteAllMessages(_state.value.messageType)

                    if (request.status == HttpStatusCode.OK) {
                        when (_state.value.messageType) {
                            MessageType.NEWS -> _newsPagingSource?.invalidate()
                            MessageType.NOTIFICATIONS -> _notificationsPagingSource?.invalidate()
                            else -> Unit
                        }
                    }
                } catch (_: Exception) {

                }
            }
        }

        fun markRead(id: Long, isRead: Int) {
            viewModelScope.launch {
                _updatingNotificationMap.update {
                    it + (id to NotificationUpdateType.MarkingRead(AsyncData.Loading))
                }

                try {
                    val request = Network.profile.markRead(id, isRead)

                    if (request.status == HttpStatusCode.OK) {
                        _updatingNotificationMap.update {
                            it + (id to NotificationUpdateType.MarkingRead(AsyncData.Success(isRead == 1)))
                        }
                    } else {
                        _updatingNotificationMap.update { it - id }
                    }
                } catch (_: Exception) {
                    _updatingNotificationMap.update { it - id }
                }
            }
        }

        fun markAllRead(items: List<Message>) {
            viewModelScope.launch {
                _updatingNotificationMap.value = items
                    .filter { it.read.getValue() == false }
                    .map { it.id }
                    .associateWith { NotificationUpdateType.MarkingRead(AsyncData.Loading) }

                try {
                    val request = Network.profile.markAllRead(_state.value.messageType)

                    if (request.status == HttpStatusCode.OK) {
                        _updatingNotificationMap.update { emptyMap() }

                        when (_state.value.messageType) {
                            MessageType.NEWS -> _newsPagingSource?.invalidate()
                            MessageType.NOTIFICATIONS -> _notificationsPagingSource?.invalidate()
                            else -> Unit
                        }
                    }
                } catch (_: Exception) {

                }
            }
        }

        fun pickTab(tab: MessageType) = _state.update { it.copy(messageType = tab) }
        fun showDialogDelete() = updateState { it.copy(showDeleteUserDialog = !it.showDeleteUserDialog) }
        fun showDialogDeleteAll() = _state.update {
            it.copy(showDeleteAll = !it.showDeleteAll)
        }
    }

    sealed interface NotificationUpdateType {
        data class MarkingRead(val state: AsyncData<Boolean>) : NotificationUpdateType
        data class Deleting(val state: AsyncData<Boolean>) : NotificationUpdateType
    }
}