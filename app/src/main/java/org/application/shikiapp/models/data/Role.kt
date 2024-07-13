package org.application.shikiapp.models.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Role(
    @Json(name = "roles") val roles: List<String>,
    @Json(name = "roles_russian") val rolesRussian: List<String>,
    @Json(name = "character") val character: Character?,
    @Json(name = "person") val person: Person?,
)
