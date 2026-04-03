package org.application.shikiapp.shared.models.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.shared.models.ui.list.BasicContent
import org.application.shikiapp.shared.models.ui.list.Content
import org.application.shikiapp.shared.network.response.AsyncData
import org.application.shikiapp.shared.utils.ResourceText
import org.application.shikiapp.shared.utils.enums.RelationKind
import org.application.shikiapp.shared.utils.enums.VideoKind
import org.jetbrains.compose.resources.StringResource

data class Anime(
    val airedOn: String,
    val charactersAll: List<BasicContent>,
    val charactersMain: List<BasicContent>,
    val chronology: List<Content>,
    val comments: Flow<PagingData<Comment>>,
    val description: AnnotatedString,
    val duration: ResourceText?,
    val episodes: String,
    val fandubbers: List<String>,
    val fansubbers: List<String>,
    val favoured: AsyncData<Boolean>,
    val franchise: String,
    val franchiseList: Map<RelationKind, List<Franchise>>,
    val genres: List<String>?,
    val id: String,
    val kind: StringResource,
    val licenseName: String,
    val licensors: List<String>,
    val links: List<ExternalLink>,
    val nextEpisodeAt: String,
    val origin: StringResource,
    val personAll: List<Content>,
    val personMain: List<Content>,
    val poster: String,
    val rating: StringResource,
    val related: List<Related>,
    val releasedOn: String,
    val score: String,
    val screenshots: List<String>,
    val similar: List<Content>,
    val stats: Pair<Statistics?, Statistics?>,
    val status: StringResource,
    val studio: Studio?,
    val title: String,
    val userRate: AsyncData<UserRate?>,
    val url: String,
    val video: List<Video>,
    val videoGrouped: Map<VideoKind, List<Video>>
)

interface AnimeT {
    val topicId: Long?
}