package org.application.shikiapp.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Role(
    @SerialName("roles") val roles: List<String>,
    @SerialName("roles_russian") val rolesRussian: List<String>,
    @SerialName("character") val character: Character?,
    @SerialName("person") val person: Person?,
)
