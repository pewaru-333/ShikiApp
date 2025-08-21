package org.application.shikiapp.models.states

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

interface UserRateState {
    val incrementId: Long?
    val updatingId: Long?
    val deletingId: Long?

    fun isIncrementing(rateId: Long): Boolean
    fun isUpdating(rateId: Long): Boolean
    fun isDeleting(rateId: Long): Boolean
    fun isEditing(rateId: Long): Boolean
}

sealed interface UserRateUiEvent {
    data class IncrementStart(val rateId: Long) : UserRateUiEvent
    data object IncrementFinish : UserRateUiEvent

    data class UpdateStart(val rateId: Long) : UserRateUiEvent
    data object UpdateFinish : UserRateUiEvent

    data class DeleteStart(val rateId: Long) : UserRateUiEvent
    data object DeleteFinish : UserRateUiEvent

    data object Error : UserRateUiEvent
}

class UserRateImpl : UserRateState {
    override var incrementId by mutableStateOf<Long?>(null)
    override var updatingId by mutableStateOf<Long?>(null)
    override var deletingId by mutableStateOf<Long?>(null)

    override fun isIncrementing(rateId: Long) = rateId == incrementId
    override fun isUpdating(rateId: Long) = rateId == updatingId
    override fun isDeleting(rateId: Long) = rateId == deletingId
    override fun isEditing(rateId: Long) = isUpdating(rateId) || isDeleting(rateId)

    fun onIncrementStart(rateId: Long) {
        incrementId = rateId
    }

    fun onIncrementFinish() {
        incrementId = null
    }

    fun onUpdateStart(rateId: Long) {
        updatingId = rateId
    }

    fun onUpdateFinish() {
        updatingId = null
    }

    fun onDeleteStart(rateId: Long) {
        deletingId = rateId
    }

    fun onDeleteFinish() {
        deletingId = null
    }

    companion object {
        val Saver: Saver<UserRateImpl, Pair<Long?, Long?>> = Saver(
            save = { it.incrementId to it.updatingId },
            restore = {
                UserRateImpl().apply {
                    incrementId = it.first
                    updatingId = it.second
                }
            }
        )
    }
}

@Composable
fun rememberRateState(): UserRateImpl {
    return rememberSaveable(saver = UserRateImpl.Saver) {
        UserRateImpl()
    }
}