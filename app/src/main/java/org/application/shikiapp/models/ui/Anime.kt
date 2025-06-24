package org.application.shikiapp.models.ui

import androidx.annotation.StringRes
import androidx.compose.ui.text.AnnotatedString
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.generated.AnimeQuery
import org.application.shikiapp.generated.AnimeStatsQuery
import org.application.shikiapp.models.data.Comment

data class Anime(
    val id: String,
    val title: String,
    val poster: String,
    val description: AnnotatedString,
    @StringRes val status: Int,
    @StringRes val kind: Int,
    val episodes: String,
    val studio: String,
    val score: String,
    @StringRes val rating: Int,
    val favoured: Boolean,
    val genres: List<AnimeQuery.Data.Anime.Genre>?,
    val related: List<Related>,
    val similar: List<Similar>,
    val charactersMain: List<CharacterMain>,
    val charactersAll: List<CharacterMain>,
    val personMain: List<PersonMain>,
    val personAll: List<PersonMain>,
    val links: List<ExternalLink>,
    val comments: Flow<PagingData<Comment>>,
    val screenshots: List<String>,
    val videos: List<AnimeQuery.Data.Anime.Video>,
    val stats: Pair<Statistics?, Statistics?>,
    val userRate: AnimeQuery.Data.Anime.UserRate?
)