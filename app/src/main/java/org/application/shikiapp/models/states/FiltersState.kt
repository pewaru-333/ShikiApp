package org.application.shikiapp.models.states

import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.snapshots.SnapshotStateSet
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.enums.Order
import org.application.shikiapp.utils.enums.PeopleFilterItem

data class FiltersState(
    val order: Order = Order.RANKED,
    val kind: SnapshotStateSet<String> = mutableStateSetOf(),
    val status: SnapshotStateSet<String> = mutableStateSetOf(),
    val seasonYS: String = BLANK,
    val seasonYF: String = BLANK,
    val seasonS: SnapshotStateSet<String> = mutableStateSetOf(),
    val season: SnapshotStateSet<String> = mutableStateSetOf(),
    val score: Float = 6f,
    val duration: SnapshotStateSet<String> = mutableStateSetOf(),
    val rating: SnapshotStateSet<String> = mutableStateSetOf(),
    val genres: SnapshotStateSet<String> = mutableStateSetOf(),
    val studio: String? = null,
    val publisher: String? = null,
    val franchise: String? = null,
    val censored: Boolean? = null,
    val myList: String? = null,
    val roles: SnapshotStateSet<PeopleFilterItem> = mutableStateSetOf(),
    val title: String = BLANK
)