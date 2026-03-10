package org.application.shikiapp.shared.network.calls.repository

import org.application.shikiapp.shared.models.ui.CharacterT

interface CharacterRepository {
    suspend fun getCharacter(id: String): CharacterT
}