package org.application.shikiapp.shared

data class AppConfig(
    val baseUrl: String,
    val urlMirrors: List<String>,
    val userAgent: String,
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val yggdrasilAddress: String?,
    val authScopes: Set<String>,
)