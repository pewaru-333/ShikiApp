package org.application.shikiapp.shared.network.calls.repository

import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.shared.models.ui.AnimeT

interface AnimeRepository {
    fun getAnimeRawData(id: String): Flow<AnimeT>
}