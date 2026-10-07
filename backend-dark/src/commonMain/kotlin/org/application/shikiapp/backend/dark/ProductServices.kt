package org.application.shikiapp.backend.dark

import org.application.shikiapp.shared.AppConfig
import org.application.shikiapp.shared.AppServices
import org.application.shikiapp.shared.network.calls.dark.IAnimeRepository
import org.application.shikiapp.shared.network.calls.dark.ICharacterRepository
import org.application.shikiapp.shared.network.calls.dark.IMangaRepository
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.client.YggdrasilTransport

object ProductServices {
    private val config = AppConfig(
        baseUrl = "https://shikimori.rip",
        urlMirrors = listOf("https://shikimori.online", "https://shikimori.net"),
        userAgent = "DarkShiki",
        clientId = "d8W9rjFLuEZKx_dYXzJ42nGvsUckx4vfhMu5Liyr7MY",
        clientSecret = "Sog_CyJs19eCuFbIvg06Gb8zu8AMXZE8VI2CKLN1td4",
        redirectUri = "darkshiki://auth/login",
        yggdrasilAddress = "http://[201:601c:da30:ddb7:77a0:9bb0:135f:b1e2]",
        authScopes = setOf("user_rates", "messages", "comments", "topics", "clubs", "friends", "ignores"),
    )

    fun create(yggdrasilTransport: YggdrasilTransport? = null) = AppServices(
        config = config,
        animeRepository = { IAnimeRepository(Network.apollo) },
        mangaRepository = { IMangaRepository(Network.apollo) },
        characterRepository = { ICharacterRepository(Network.apollo) },
        yggdrasilTransport = yggdrasilTransport
    )
}
