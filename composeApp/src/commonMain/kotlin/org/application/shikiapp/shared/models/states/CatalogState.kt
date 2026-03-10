package org.application.shikiapp.shared.models.states

import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.enums.CatalogItem

data class CatalogState(
    val menu: CatalogItem = CatalogItem.ANIME,
    val search: String = BLANK,
    val dialogFilter: DialogFilters? = null,
    val expandedFilters: Set<ExpandedFilters> = emptySet()
)

val CatalogState.isFiltersVisible: Boolean
    get() = dialogFilter != null && dialogFilter != DialogFilters.People

sealed interface DialogFilters {
    data object Anime : DialogFilters
    data object Manga : DialogFilters
    data object Ranobe : DialogFilters
    data object People : DialogFilters
}

sealed interface ExpandedFilters {
    data object Status : ExpandedFilters
    data object Kind : ExpandedFilters
    data object Season : ExpandedFilters
    data object Score : ExpandedFilters
    data object Duration : ExpandedFilters
    data object Rating : ExpandedFilters
    data object Genres : ExpandedFilters
}