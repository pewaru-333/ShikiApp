package org.application.shikiapp.shared.models.states

data class NewsDetailState(
    val showComments: Boolean = false,
    val showImage: Boolean = false,
    val image: Int = 0
)