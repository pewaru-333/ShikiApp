package org.application.shikiapp.models.states

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.ORDERS
import org.application.shikiapp.utils.enums.PeopleFilterItems

data class FiltersState(
    val order: String = ORDERS.keys.elementAt(2),
    val orderName: String = ORDERS.values.elementAt(2),
    val kind: SnapshotStateList<String> = mutableStateListOf(),
    val status: SnapshotStateList<String> = mutableStateListOf(),
    val seasonYS: String = BLANK,
    val seasonYF: String = BLANK,
    val seasonS: SnapshotStateList<String> = mutableStateListOf(),
    val season: SnapshotStateList<String> = mutableStateListOf(),
    val score: Float = 6f,
    val duration: SnapshotStateList<String> = mutableStateListOf(),
    val rating: SnapshotStateList<String> = mutableStateListOf(),
    val genres: SnapshotStateList<String> = mutableStateListOf(),
    val studio: String? = null,
    val publisher: String? = null,
    val franchise: String? = null,
    val censored: Boolean? = null,
    val myList: String? = null,
    val isSeyu: Boolean? = null,
    val isProducer: Boolean? = null,
    val isMangaka: Boolean? = null,
    val roles: SnapshotStateList<PeopleFilterItems> = mutableStateListOf(),
    val title: String = BLANK
)