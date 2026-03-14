package org.application.shikiapp.shared.network.calls.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.shared.models.data.Franchise
import org.application.shikiapp.shared.models.data.MangaBasic
import org.application.shikiapp.shared.models.ui.Comment
import org.application.shikiapp.shared.models.ui.Manga
import org.application.shikiapp.shared.models.ui.MangaT

interface MangaRepository {
    fun getMangaRawData(id: String): Flow<MangaT>

    fun mapToManga(
        raw: MangaT,
        franchise: Franchise,
        similar: List<MangaBasic>,
        favoured: Boolean,
        comments: Flow<PagingData<Comment>>
    ): Manga
}