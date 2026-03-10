package org.application.shikiapp.shared.models.states

data class CharacterState(
    val showComments: Boolean = false,
    val showPoster: Boolean = false,
    val showRelated: Boolean = false,
    val showSeyu: Boolean = false,
    val showSheet: Boolean = false
)