package org.application.shikiapp.models.ui

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.AnimeQuery
import org.application.AnimeStatsQuery
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.ExternalLink

data class Anime(
    val id: String,
    val title: String,
    val poster: String?,
    val description: String,
    val status: String,
    val kind: String,
    val episodes: String,
    val studio: String,
    val score: String,
    val rating: String,
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
    val stats: AnimeStatsQuery.Data.Anime,
    val userRate: AnimeQuery.Data.Anime.UserRate?
)
