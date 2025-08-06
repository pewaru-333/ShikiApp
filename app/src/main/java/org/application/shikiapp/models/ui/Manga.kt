package org.application.shikiapp.models.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.utils.enums.Kind
import org.application.shikiapp.utils.enums.RelationKind

data class Manga(
    val airedOn: String,
    val chapters: String,
    val charactersAll: List<BasicContent>,
    val charactersMain: List<BasicContent>,
    val chronology: List<Content>,
    val comments: Flow<PagingData<Comment>>,
    val description: AnnotatedString,
    val favoured: Boolean,
    val franchise: String,
    val franchiseList: Map<RelationKind, List<Franchise>>,
    val genres: List<String>?,
    val id: String,
    val isOngoing: Boolean,
    val kindEnum: Kind,
    val kindString: Int,
    val kindTitle: Int,
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
    val similar: List<BasicContent>,
    val stats: Pair<Statistics?, Statistics?>,
    val status: Int,
    val title: String,
    val userRate: UserRate?,
    val volumes: String
)
