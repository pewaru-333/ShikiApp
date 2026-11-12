package org.application.shikiapp.backend.dark

import org.application.shikiapp.shared.di.Preferences
import org.application.shikiapp.shared.utils.BLANK

internal object YggdrasilPreferences {
    val enabled by lazy { Preferences.PreferenceBoolean("yggdrasil_enabled", false) }
    val peers by lazy { Preferences.PreferenceString("yggdrasil_peers", BLANK) }
    val cachedPeers by lazy { Preferences.PreferenceString("yggdrasil_cached_peers", BLANK) }
    val privateKey by lazy { Preferences.PreferenceString("yggdrasil_private_key", BLANK) }

    val peerList: List<String> get() = peers.value.split(',')

    fun clear() {
        enabled.value = false
        peers.value = BLANK
        cachedPeers.value = BLANK
    }
}