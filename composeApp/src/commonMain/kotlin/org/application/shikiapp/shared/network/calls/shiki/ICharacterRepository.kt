package org.application.shikiapp.shared.network.calls.shiki

import com.apollographql.apollo.ApolloClient
import org.application.shikiapp.generated.shikiapp.CharacterQuery
import org.application.shikiapp.shared.models.ui.CharacterT
import org.application.shikiapp.shared.network.calls.repository.CharacterRepository

class ICharacterRepository(private val apollo: ApolloClient) : CharacterRepository {
    override suspend fun getCharacter(id: String): CharacterT {
        val character = apollo.query(CharacterQuery(id))
            .execute()
            .dataAssertNoErrors
            .characters
            .first()

        return object : CharacterT {
            override val topicId = character.topic?.id
            override val poster = character.poster?.originalUrl
        }
    }
}