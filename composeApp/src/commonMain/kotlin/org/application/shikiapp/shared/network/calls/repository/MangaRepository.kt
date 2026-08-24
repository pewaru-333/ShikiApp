package org.application.shikiapp.shared.network.calls.repository

import kotlinx.coroutines.flow.Flow
import org.application.shikiapp.shared.models.ui.MangaT

interface MangaRepository {
    fun getMangaRawData(id: String): Flow<MangaT>
}