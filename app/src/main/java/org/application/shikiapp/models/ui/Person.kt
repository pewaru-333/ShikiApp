package org.application.shikiapp.models.ui

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.data.Comment

data class Person(
    val id: Long,
    val russian: String?,
    val english: String?,
    val japanese: String,
    val birthday: String?,
    val deathday: String?,
    val jobTitle: String,
    val image: String,
    val website: String,
    val personKind: String,
    val grouppedRoles: List<List<String>>,
    val characters: List<CharacterMain>,
    val comments: Flow<PagingData<Comment>>,
    val isPersonFavoured: Boolean
)

data class PersonMain(
    val id: Long,
    val name: String,
    val poster: String?
)