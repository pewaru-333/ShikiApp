package org.application.shikiapp.models.viewModels

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.application.shikiapp.di.Preferences
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.data.FullMessage
import org.application.shikiapp.models.data.MessageToSend
import org.application.shikiapp.models.data.MessageToSendShort
import org.application.shikiapp.models.states.UserMessagesState
import org.application.shikiapp.models.ui.list.Dialog
import org.application.shikiapp.models.ui.mappers.toDialog
import org.application.shikiapp.models.ui.mappers.toDialogMessage
import org.application.shikiapp.network.client.Network
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.BLANK

class UserMessagesViewModel() : ContentDetailViewModel<List<Dialog>, UserMessagesState>() {
    private val _messages = MutableStateFlow<Response<List<Dialog>, Throwable>>(Response.Loading)
    val messages = _messages.asStateFlow()

    override fun loadData() {
        viewModelScope.launch {
            emit(Response.Loading)

            try {
                val dialogs = Network.profile.getDialogs()
                    .map(org.application.shikiapp.models.data.Dialog::toDialog)

                emit(Response.Success(dialogs))
            } catch (e: Throwable) {
                emit(Response.Error(e))
            }
        }
    }

    override fun initState() = UserMessagesState()
    override fun onEvent(event: ContentDetailEvent) = Unit

    fun showDialogs() = updateState { it.copy(dialogId = null) }
    fun setText(text: String) = updateState { it.copy(text = text) }
    fun showDialogDelete(nickname: String? = null) = updateState { it.copy(toDeleteId = nickname) }

    fun getDialog(userId: Long = state.value.userId, nickname: String = state.value.dialogId ?: BLANK) {
        updateState {
            it.copy(
                text = BLANK,
                dialogId = nickname,
                userId = userId
            )
        }

        viewModelScope.launch {
           _messages.emit(Response.Loading)

            try {
                val messages = Network.profile.getUserDialog(nickname)
                    .map(FullMessage::toDialogMessage)

               _messages.emit(Response.Success(messages))
            } catch (e: Exception) {
               _messages.emit(Response.Error(e))
            }
        }
    }

    fun sendMessage() {
        viewModelScope.launch {
            try {
                val messageToSend = MessageToSend(
                    frontend = "false",
                    message = MessageToSendShort(
                        body = state.value.text,
                        kind = "Private",
                        fromId = Preferences.userId,
                        toId = state.value.userId
                    )
                )

                Network.profile.sendMessage(messageToSend)
            } catch (_: Exception) {

            } finally {
                loadData()
                getDialog()
            }
        }
    }

//    fun deleteMessage() {
//        viewModelScope.launch {
//            try {
//                Network.profile.deleteMessage(state.value.messageId)
//            } catch (_: Exception) {
//
//            } finally {
//                getDialog()
//            }
//        }
//    }

    fun removeDialog() {
        viewModelScope.launch {
            try {
                state.value.toDeleteId?.let { Network.profile.deleteUserDialog(it) }
            } catch (_: Exception) {

            } finally {
                updateState { it.copy(toDeleteId = null) }
                loadData()
            }
        }
    }
}