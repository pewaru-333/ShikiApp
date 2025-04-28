package org.application.shikiapp.models.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.ui.list.Content

data class Character(
    val id: String,
    val russian: String?,
    val japanese: String?,
    val altName: String?,
    val description: AnnotatedString,
    val poster: String,
    val favoured: Boolean,
    val anime: List<Content>,
    val manga: List<Content>,
    val seyu: List<Content>,
    val comments: Flow<PagingData<Comment>>
)

data class CharacterMain(
    val id: String,
    val name: String,
    val poster: String
)
