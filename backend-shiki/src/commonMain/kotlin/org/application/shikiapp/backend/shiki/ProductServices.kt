package org.application.shikiapp.backend.shiki

import org.application.shikiapp.shared.AppConfig
import org.application.shikiapp.shared.AppServices
import org.application.shikiapp.shared.network.calls.shiki.IAnimeRepository
import org.application.shikiapp.shared.network.calls.shiki.ICharacterRepository
import org.application.shikiapp.shared.network.calls.shiki.IMangaRepository
import org.application.shikiapp.shared.network.client.Network

object ProductServices {
    private val config = AppConfig(
        baseUrl = "https://shikimori.io",
        urlMirrors = listOf("https://shikimori.one", "https://shiki.one"),
        userAgent = "ShikiApp",
        clientId = "C0IlIBQYqt9VHjuoayfbBG9ulhBH9XWuTOxSX_6oE6g",
        clientSecret = "0U2MtkFgtGUP9_TFKBw1ORVy6S68KZDz_AdKsoMfnFM",
        redirectUri = "app://login",
        yggdrasilAddress = null,
        authScopes = setOf("user_rates", "messages", "comments", "topics", "clubs", "friends")
    )

    fun create(redirectUri: String = config.redirectUri) = AppServices(
        config = config.copy(redirectUri = redirectUri),
        animeRepository = { IAnimeRepository(Network.apollo) },
        mangaRepository = { IMangaRepository(Network.apollo) },
        characterRepository = { ICharacterRepository(Network.apollo) },
    )
}
