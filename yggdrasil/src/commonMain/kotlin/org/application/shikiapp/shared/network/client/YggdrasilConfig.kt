package org.application.shikiapp.shared.network.client

data class YggdrasilConfig(
    val enabled: Boolean,
    val peers: List<String>,
    val privateKeyPem: String?
)
