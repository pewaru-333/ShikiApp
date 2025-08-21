package org.application.shikiapp.models.ui

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.models.ui.list.BasicContent
import org.application.shikiapp.network.response.AsyncData
import org.application.shikiapp.utils.enums.LinkedType

data class Person(
    val birthday: String?,
    val characters: List<BasicContent>,
    val comments: Flow<PagingData<Comment>>,
    val deathday: String?,
    val english: String?,
    val favoured: AsyncData<Boolean>,
    val grouppedRoles: List<List<String>>,
    val id: Long,
    val image: String,
    val japanese: String,
    val jobTitle: String,
    val personKind: String,
    val relatedList: List<Related>,
    val relatedMap: Map<LinkedType, List<Related>>,
    val russian: String?,
    val url: String,
    val website: String
)