package org.application.shikiapp.models.states

import org.application.shikiapp.utils.enums.ClubMenu

data class ClubState(
    val menu: ClubMenu? = null,
    val image: String? = null,
    val isMember: Boolean = false,
    val showClubs: Boolean = false,
    val showBottomSheet: Boolean = false,
    val showComments: Boolean = false,
    val showFullImage: Boolean = false,
)

val ClubState.showMembers: Boolean
    get() = menu == ClubMenu.MEMBERS

val ClubState.showContent: Boolean
    get() = menu in listOf(ClubMenu.ANIME, ClubMenu.MANGA, ClubMenu.RANOBE, ClubMenu.CHARACTERS)

val ClubState.showImages: Boolean
    get() = menu == ClubMenu.IMAGES