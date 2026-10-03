package org.application.shikiapp.shared

import org.application.shikiapp.shared.network.calls.repository.AnimeRepository
import org.application.shikiapp.shared.network.calls.repository.CharacterRepository
import org.application.shikiapp.shared.network.calls.repository.MangaRepository
import org.application.shikiapp.shared.network.client.YggdrasilTransport

class AppServices(
    val config: AppConfig,
    animeRepository: () -> AnimeRepository,
    mangaRepository: () -> MangaRepository,
    characterRepository: () -> CharacterRepository,
    val yggdrasilTransport: YggdrasilTransport? = null
) {
    val animeRepository: AnimeRepository by lazy(animeRepository)
    val mangaRepository: MangaRepository by lazy(mangaRepository)
    val characterRepository: CharacterRepository by lazy(characterRepository)
}
