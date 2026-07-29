package org.application.shikiapp.shared.models.states

import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.enums.Order
import org.application.shikiapp.shared.utils.enums.PeopleFilterItem

data class FiltersState(
    val order: Order = Preferences.lastCatalogOrder,
    val kind: Set<String> = emptySet(),
    val status: Set<String> = emptySet(),
    val seasonYearStart: String = BLANK,
    val seasonYearFinal: String = BLANK,
    val seasonYearSeason: Set<String> = emptySet(),
    val seasonSet: Set<String> = emptySet(),
    val score: Float = 1f,
    val duration: Set<String> = emptySet(),
    val rating: Set<String> = emptySet(),
    val genres: Set<String> = emptySet(),
    val studio: String? = null,
    val publisher: String? = null,
    val franchise: String? = null,
    val censored: Boolean? = null,
    val myList: String? = null,
    val roles: Set<PeopleFilterItem> = emptySet(),
    val title: String = BLANK
)