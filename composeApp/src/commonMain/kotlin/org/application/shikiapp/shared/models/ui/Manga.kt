package org.application.shikiapp.shared.models.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.models.ui.list.Content
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.utils.enums.Kind
import org.application.shikiapp.shared.utils.enums.RelationKind
import org.jetbrains.compose.resources.StringResource

data class Manga(
    val airedOn: String,
    val chapters: String,
    val charactersAll: List<BasicContent>,
    val charactersMain: List<BasicContent>,
    val chronology: List<Content>,
    val comments: Flow<PagingData<Comment>>,
    val description: AnnotatedString,
    val favoured: AsyncData<Boolean>,
    val franchise: String,
    val franchiseList: Map<RelationKind, List<Franchise>>,
    val genres: List<String>?,
    val id: String,
    val isOngoing: Boolean,
    val kindEnum: Kind,
    val kindString: StringResource,
    val kindTitle: StringResource,
    val licenseName: String,
    val licensors: List<String>,
    val links: List<ExternalLink>,
    val personAll: List<BasicContent>,
    val personMain: List<BasicContent>,
    val poster: String,
    val publisher: Publisher?,
    val related: List<Related>,
    val releasedOn: String,
    val score: String,
    val similar: List<Content>,
    val stats: Pair<Statistics?, Statistics?>,
    val status: StringResource,
    val title: String,
    val userRate: AsyncData<UserRate?>,
    val url: String,
    val volumes: String
)

interface MangaT {
    val topicId: Long?
}