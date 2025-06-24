@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.models.states

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.ui.unit.Density
import org.application.shikiapp.utils.enums.ClubMenu

data class ClubState(
    val menu: ClubMenu? = null,
    val image: String? = null,
    val isMember: Boolean = false,
    val showClubs: Boolean = false,
    val showBottomSheet: Boolean = false,
    val showComments: Boolean = false,
    val showFullImage: Boolean = false,
    val sheetState: SheetState = SheetState(false, Density(1f))
)

val ClubState.showMembers: Boolean
    get() = menu == ClubMenu.MEMBERS

val ClubState.showContent: Boolean
    get() = menu in listOf(ClubMenu.ANIME, ClubMenu.MANGA, ClubMenu.RANOBE, ClubMenu.CHARACTERS)

val ClubState.showImages: Boolean
    get() = menu == ClubMenu.IMAGES