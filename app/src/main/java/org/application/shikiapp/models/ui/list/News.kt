package org.application.shikiapp.models.ui.list

data class News(
    val id: Long,
    val title: String,
    val poster: String?,
    val date: String,
    val author: String
)
