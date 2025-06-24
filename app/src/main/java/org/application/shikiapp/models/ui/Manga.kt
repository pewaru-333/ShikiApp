package org.application.shikiapp.models.ui

import androidx.annotation.StringRes
import androidx.compose.ui.text.AnnotatedString
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.generated.MangaQuery
import org.application.shikiapp.generated.type.MangaKindEnum
import org.application.shikiapp.models.data.Comment

data class Manga(
    val id: String,
    val title: String,
    val kindEnum: MangaKindEnum?,
    @StringRes val kindString: Int,
    @StringRes val kindTitle: Int,
    val volumes: String,
    val chapters: String,
    @StringRes val status: Int,
    val poster: String,
    val publisher: String,
    val score: String,
    val description: AnnotatedString,
    val favoured: Boolean,
    val showChapters: Boolean,
    val genres: List<MangaQuery.Data.Manga.Genre>?,
    val similar: List<Similar>,
    val related: List<Related>,
    val links: List<ExternalLink>,
    val characterMain: List<CharacterMain>,
    val charactersAll: List<CharacterMain>,
    val personMain: List<PersonMain>,
    val personAll: List<PersonMain>,
    val stats: Pair<Statistics?, Statistics?>,
    val comments: Flow<PagingData<Comment>>,
    val userRate: MangaQuery.Data.Manga.UserRate?
)
