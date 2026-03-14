package org.application.shikiapp.shared.network.calls.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.shared.models.data.AnimeBasic
import org.application.shikiapp.shared.models.data.Franchise
import org.application.shikiapp.shared.models.ui.Anime
import org.application.shikiapp.shared.models.ui.AnimeT
import org.application.shikiapp.shared.models.ui.Comment

interface AnimeRepository {
    fun getAnimeRawData(id: String): Flow<AnimeT>

    fun mapToAnime(
        raw: AnimeT,
        franchise: Franchise,
        similar: List<AnimeBasic>,
        favoured: Boolean,
        comments: Flow<PagingData<Comment>>
    ): Anime
}