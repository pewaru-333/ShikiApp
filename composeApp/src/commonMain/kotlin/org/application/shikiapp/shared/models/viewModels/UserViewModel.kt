@file:OptIn(ExperimentalCoroutinesApi::class)

package org.application.shikiapp.shared.models.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.*
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.io.IOException
import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.data.FullMessage
import org.application.shikiapp.shared.models.data.MessageToSend
import org.application.shikiapp.shared.models.data.MessageToSendShort
import org.application.shikiapp.shared.models.data.UserBasic
import org.application.shikiapp.shared.models.states.BaseDialogState
import org.application.shikiapp.shared.models.states.UserMessagesState
import org.application.shikiapp.shared.models.states.UserState
import org.application.shikiapp.shared.models.ui.History
import org.application.shikiapp.shared.models.ui.User
import org.application.shikiapp.shared.models.ui.list.Dialog
import org.application.shikiapp.shared.models.ui.list.Message
import org.application.shikiapp.shared.models.ui.mappers.*
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.paging.CommonPaging
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.network.response.Response
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.enums.CommentableType
import org.application.shikiapp.shared.utils.enums.MessageType
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.ui.Formatter
import org.application.shikiapp.shared.utils.ui.HtmlParser
import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_successfully_deleted_dialog
import shikiapp.composeapp.generated.resources.text_unsuccessfully_deleted_dialog

open class UserViewModel(private val saved: SavedStateHandle) : ContentDetailViewModel<User, UserState>() {
    private val _userId = MutableStateFlow<Long?>(null)

    open val userId: Long
        get() = _userId.value ?: saved.toRoute<Screen.User>().id

    var mailManager = MailManager()
        private set

    protected val friends = _userId.flatMapLatest { id ->
        if (id == null) flowOf(PagingData.empty())
        else Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommonPaging(UserBasic::id) { page, params ->
                    Network.user.getFriends(id, page, params.loadSize)
                }
            }
        ).flow.onStart { emit(PagingData.empty()) }
    }.cachedIn(viewModelScope)

    protected val history = _userId.flatMapLatest { id ->
        if (id == null) flowOf(PagingData.empty())
        else Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommonPaging(History::id) { page, params ->
                    Network.user.getHistory(id, page, params.loadSize)
                        .map(org.application.shikiapp.shared.models.data.History::mapper)
                }
            }
        ).flow.onStart { emit(PagingData.empty()) }
    }.cachedIn(viewModelScope)

    protected fun setUserParams(id: Long) {
        _userId.value = id
        setCommentParams(id, CommentableType.USER)
    }

    protected fun resetUserData() {
        _userId.value = null
        setCommentParams(null, CommentableType.USER)
        mailManager.close()
        mailManager = MailManager()
        updateState { UserState() }
    }

    protected suspend fun loadUser(id: Long, profile: org.application.shikiapp.shared.models.data.User? = null): User = coroutineScope {
        val user = async { profile ?: Network.user.getUser(id.toString()) }
        val clubs = async { Network.user.getClubs(id) }
        val favourites = async { Network.user.getFavourites(id).toBasicContentMap() }

        user.await().mapper(
            clubs = clubs.await(),
            comments = comments,
            friends = friends,
            history = history,
            favourites = favourites.await()
        )
    }

    override val contentId = Any()

    override fun initState() = UserState()

    override fun loadData() {
        viewModelScope.launch {
            if (response.value !is Response.Success) {
                emit(Response.Loading)
            }

            try {
                val currentId = userId
                val profile = if (currentId > 0L) null
                else {
                    val nickname = saved.toRoute<Screen.User>().nickname ?: throw IOException()
                    Network.user.getUser(nickname, isNickname = true)
                }

                val id = profile?.id ?: currentId
                if (id <= 0L) throw IOException()

                setUserParams(id)
                val userLoaded = loadUser(id, profile)

                updateState {
                    it.copy(
                        isFriend = userLoaded.inFriends
                    )
                }

                emit(Response.Success(userLoaded))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                emit(Response.Error(e))
            }
        }
    }

    override fun onEvent(event: ContentDetailEvent) {
        super.onEvent(event)

        when (event) {
            is ContentDetailEvent.ToggleDialog if event.dialogState is BaseDialogState.User.DialogUser -> {
                mailManager.getDialog(event.dialogState.userId)
            }

            ContentDetailEvent.User.ToggleFriend -> toggleFriend()

            else -> Unit
        }
    }

    fun toggleFriend() {
        updateState { it.copy(dialogState = null) }
        viewModelScope.launch {
            try {
                if (state.value.isFriend) Network.user.removeFriend(userId)
                else Network.user.addFriend(userId)
            } catch (_: Exception) {

            } finally {
                loadData()
            }
        }
    }

    inner class MailManager {
        private val scope = CoroutineScope(viewModelScope.coroutineContext + SupervisorJob(viewModelScope.coroutineContext[Job]))

        private val _updatingNotificationMap = MutableStateFlow<Map<Long, NotificationUpdateType>>(emptyMap())

        private val _dialogs = MutableStateFlow<Response<List<Dialog>, Exception>>(Response.Loading)
        val dialogs = _dialogs.stateIn(scope, SharingStarted.WhileSubscribed(5000L), Response.Loading)

        private val _state = MutableStateFlow(UserMessagesState())
        val state = _state.stateIn(scope, SharingStarted.WhileSubscribed(5000L), UserMessagesState())

        private val _newMessages = MutableStateFlow<List<Dialog>>(emptyList())
        val newMessages = _newMessages.asStateFlow()

        private val _dialogDeleteError = Channel<StringResource>()
        val dialogDeleteError = _dialogDeleteError.receiveAsFlow()

        val oldMessages = _state.flatMapLatest { state ->
            if (state.userId == -1L) flowOf(PagingData.empty())
            else Pager(
                config = PagingConfig(pageSize = 30, enablePlaceholders = false),
                pagingSourceFactory = {
                    CommonPaging(Dialog::id) { page, params ->
                        Network.profile.getUserDialog(state.userId, page, params.loadSize)
                            .map(FullMessage::toDialogMessage)
                    }
                }
            ).flow
                .onStart { _newMessages.emit(emptyList()) }
                .cachedIn(scope)
                .retryWhen { cause, attempt -> cause is ClientRequestException || attempt <= 3 }
        }

        private var _newsPagingSource: CommonPaging<*>? = null
        val news = Pager(
            config = PagingConfig(pageSize = 15, enablePlaceholders = false),
            pagingSourceFactory = {
                CommonPaging(Message::id) { page, params ->
                    Network.profile.getMessages(Preferences.userId.value, MessageType.NEWS, page, params.loadSize)
                        .map(FullMessage::toNewsMessage)
                }.also { _newsPagingSource = it }
            }
        ).flow
            .cachedIn(scope)
            .combine(_updatingNotificationMap) { pagingData, map ->
                if (map.isEmpty()) return@combine pagingData

                pagingData.map { message ->
                    when (val data = map[message.id]) {
                        is NotificationUpdateType.MarkingRead -> message.copy(read = data.state)
                        is NotificationUpdateType.Deleting -> message.copy(isDeleting = data.state)
                        null -> message
                    }
                }.filter { it.isDeleting.getValue() != true }
            }
            .cachedIn(scope)

        private var _notificationsPagingSource: CommonPaging<*>? = null
        val notifications = Pager(
            config = PagingConfig(pageSize = 15, enablePlaceholders = false),
            pagingSourceFactory = {
                CommonPaging(Message::id) { page, params ->
                    Network.profile.getMessages(Preferences.userId.value, MessageType.NOTIFICATIONS, page, params.loadSize)
                        .map(FullMessage::toNewsMessage)
                }.also { _notificationsPagingSource = it }
            }
        ).flow
            .cachedIn(scope)
            .combine(_updatingNotificationMap) { pagingData, map ->
                if (map.isEmpty()) return@combine pagingData

                pagingData.map { message ->
                    when (val data = map[message.id]) {
                        is NotificationUpdateType.MarkingRead -> message.copy(read = data.state)
                        is NotificationUpdateType.Deleting -> message.copy(isDeleting = data.state)
                        null -> message
                    }
                }.filter { it.isDeleting.getValue() != true }
            }
            .cachedIn(scope)

        fun close() = scope.cancel()

        fun loadData() {
            scope.launch {
                if (dialogs.value !is Response.Success) {
                   _dialogs.emit(Response.Loading)
                }

                try {
                    val dialogs = Network.profile.getDialogs()
                        .map(org.application.shikiapp.shared.models.data.Dialog::toDialog)

                    _dialogs.emit(Response.Success(dialogs))
                } catch (e: Exception) {
                    _dialogs.emit(Response.Error(e))
                }
            }
        }

        fun getDialog(userId: Long, isFromList: Boolean = false) {
            updateState { it.copy(dialogState = BaseDialogState.User.DialogUser(userId)) }

            scope.launch {
                _state.update {
                    it.copy(
                        userId = userId,
                        userNickname = AsyncData.Loading,
                        userAvatar = AsyncData.Loading,
                        isFromList = isFromList
                    )
                }

                try {
                    val (userNickname, userAvatar, lastOnline) = with(Network.user.getUser(userId.toString())) {
                        Triple(nickname, avatar, lastOnline)
                    }

                    _state.update {
                        it.copy(
                            userNickname = AsyncData.Success(userNickname),
                            userAvatar = AsyncData.Success(userAvatar),
                            lastOnlineAt = lastOnline.orEmpty()
                        )
                    }
                } catch (_: Exception) {

                }
            }
        }

        fun sendMessage(message: String) {
            scope.launch {
                val messageToSend = MessageToSend(
                    frontend = "false",
                    message = MessageToSendShort(
                        body = message,
                        kind = "Private",
                        fromId = Preferences.userId.value,
                        toId = state.value.userId
                    )
                )

                val tempMessage = Dialog(
                    id = -(_newMessages.value.size - 1).toLong(),
                    userId = Preferences.userId.value,
                    userNickname = BLANK,
                    userAvatar = BLANK,
                    lastMessages = HtmlParser.parseComment(messageToSend.message.body),
                    lastMessage = ResourceText.StaticString(BLANK),
                    lastDate = Formatter.convertDate(),
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

            scope.launch {
                try {
                    val request = Network.profile.deleteUserDialog(state.value.userId)

                    if (request.status == HttpStatusCode.OK) {
                        updateState { it.copy(dialogState = BaseDialogState.User.DialogAll) }
                        _dialogDeleteError.send(Res.string.text_successfully_deleted_dialog)
                    } else {
                        _dialogDeleteError.send(Res.string.text_unsuccessfully_deleted_dialog)
                    }
                } catch (_: Exception) {
                    _dialogDeleteError.send(Res.string.text_unsuccessfully_deleted_dialog)
                } finally {
                    loadData()
                }
            }
        }

        fun getUnreadMessages() {
            val id = Preferences.userId.value
            if (id <= 0L) return

            scope.launch {
                try {
                    val unread = Network.profile.getUnreadMessages(id)
                    if (Preferences.userId.value != id || mailManager !== this@MailManager) return@launch

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
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {

                }
            }
        }

        fun deleteMessage(id: Long) {
            scope.launch {
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

            scope.launch {
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
            scope.launch {
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
            scope.launch {
                _updatingNotificationMap.value = items
                    .mapNotNull { if (it.read.getValue() == false) it.id else null }
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