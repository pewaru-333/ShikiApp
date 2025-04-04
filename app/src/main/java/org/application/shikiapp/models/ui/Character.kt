package org.application.shikiapp.models.ui

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.data.AnimeBasic
import org.application.shikiapp.models.data.BasicInfo
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.MangaBasic

data class Character(
    val id: String,
    val russian: String?,
    val japanese: String?,
    val altName: String?,
    val description: String,
    val poster: String?,
    val favoured: Boolean,
    val anime: List<AnimeBasic>,
    val manga: List<MangaBasic>,
    val seyu: List<BasicInfo>,
    val comments: Flow<PagingData<Comment>>
)

data class CharacterMain(
    val id: String,
    val name: String,
    val poster: String?
)
