package org.application.shikiapp.shared

import coil3.intercept.Interceptor
import org.application.shikiapp.shared.network.calls.repository.AnimeRepository
import org.application.shikiapp.shared.network.calls.repository.CharacterRepository
import org.application.shikiapp.shared.network.calls.repository.MangaRepository
import org.application.shikiapp.shared.network.client.NetworkExtension
import org.application.shikiapp.shared.utils.ui.NetworkSettings

class AppServices(
    val config: AppConfig,
    animeRepository: () -> AnimeRepository,
    mangaRepository: () -> MangaRepository,
    characterRepository: () -> CharacterRepository,
    val networkExtension: NetworkExtension? = null,
    val networkSettings: (() -> NetworkSettings)? = null,
    val imageInterceptor: Interceptor? = null
) {
    val animeRepository: AnimeRepository by lazy(animeRepository)
    val mangaRepository: MangaRepository by lazy(mangaRepository)
    val characterRepository: CharacterRepository by lazy(characterRepository)
}