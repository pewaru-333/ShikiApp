package org.application.shikiapp.shared.models.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.models.ui.list.Content
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.utils.enums.LinkedType

data class Character(
    val altName: String?,
    val anime: List<Content>,
    val comments: Flow<PagingData<Comment>>,
    val description: AnnotatedString,
    val favoured: AsyncData<Boolean>,
    val id: String,
    val japanese: String?,
    val manga: List<Content>,
    val poster: String,
    val relatedList: List<Related>,
    val relatedMap: Map<LinkedType, List<Related>>,
    val russian: String?,
    val seyu: List<BasicContent>,
    val url: String
)

interface CharacterT {
    val topicId: String?
    val poster: String?
}