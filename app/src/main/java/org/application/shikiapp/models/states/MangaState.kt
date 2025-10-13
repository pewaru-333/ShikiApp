package org.application.shikiapp.models.states

data class MangaState(
    val showAuthors: Boolean = false,
    val showCharacters: Boolean = false,
    val showComments: Boolean = false,
    val showLinks: Boolean = false,
    val showRate: Boolean = false,
    val showRelated: Boolean = false,
    val showSheet: Boolean = false,
    val showSimilar: Boolean = false,
    val showStats: Boolean = false
)