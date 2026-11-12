package org.application.shikiapp.shared.network.calls.repository

import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.shared.models.ui.AnimeT
import org.application.shikiapp.shared.models.ui.Review

interface AnimeRepository {
    fun getAnimeRawData(id: String): Flow<AnimeT>

    suspend fun getReviews(id: String, url: String, page: Int): Pair<List<Review>, Int?>
}