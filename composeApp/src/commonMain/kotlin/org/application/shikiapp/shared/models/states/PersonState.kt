package org.application.shikiapp.shared.models.states

data class PersonState(
    val showCharacters: Boolean = false,
    val showComments: Boolean = false,
    val showPoster: Boolean = false,
    val showSheet: Boolean = false,
    val showWorks: Boolean = false
)