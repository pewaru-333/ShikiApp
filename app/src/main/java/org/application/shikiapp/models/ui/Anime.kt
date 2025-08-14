package org.application.shikiapp.models.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.models.ui.list.Content
import org.application.shikiapp.utils.enums.RelationKind

data class Anime(
    val airedOn: String,
    val charactersAll: List<BasicContent>,
    val charactersMain: List<BasicContent>,
    val chronology: List<Content>,
    val comments: Flow<PagingData<Comment>>,
    val description: AnnotatedString,
    val duration: String,
    val episodes: String,
    val fandubbers: List<String>,
    val fansubbers: List<String>,
    val favoured: Boolean,
    val franchise: String,
    val franchiseList: Map<RelationKind, List<Franchise>>,
    val genres: List<String>?,
    val id: String,
    val kind: Int,
    val licenseName: String,
    val licensors: List<String>,
    val links: List<ExternalLink>,
    val nextEpisodeAt: String,
    val origin: Int,
    val personAll: List<BasicContent>,
    val personMain: List<BasicContent>,
    val poster: String,
    val rating: Int,
    val related: List<Related>,
    val releasedOn: String,
    val score: String,
    val screenshots: List<String>,
    val similar: List<Content>,
    val stats: Pair<Statistics?, Statistics?>,
    val status: Int,
    val studio: Studio?,
    val title: String,
    val userRate: UserRate?,
    val url: String,
    val videos: List<Video>
)