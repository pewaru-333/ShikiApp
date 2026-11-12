package org.application.shikiapp.backend.dark

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.application.shikiapp.backend.dark.resources.Res
import org.application.shikiapp.backend.dark.resources.preference_use_yggdrasil
import org.application.shikiapp.backend.dark.resources.preference_use_yggdrasil_summary
import org.application.shikiapp.backend.dark.resources.text_yggdrasil_peer_format
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.ui.NetworkSettings
import org.application.shikiapp.shared.utils.ui.ProxyInputRow
import org.application.shikiapp.shared.utils.ui.SettingsToggleRow
import org.jetbrains.compose.resources.stringResource

@Stable
internal class YggdrasilSettings : NetworkSettings {
    override var usesOwnConnection by mutableStateOf(YggdrasilPreferences.enabled.value)
        private set

    private val peers = mutableStateListOf<String>().apply {
        addAll(YggdrasilPreferences.peerList.filter(String::isNotBlank).ifEmpty { listOf(BLANK) })
    }
    private var error by mutableStateOf(false)
    private val peerPattern = Regex("""^(tcp|tls|quic|ws|wss)://\S+$""", RegexOption.IGNORE_CASE)

    override fun save(): Boolean {
        error = usesOwnConnection && peers.any { !peerPattern.matches(it) }
        if (error) return false

        YggdrasilPreferences.enabled.value = usesOwnConnection

        if (usesOwnConnection) YggdrasilPreferences.peers.value = peers.distinct().joinToString(",")
        return true
    }

    override fun clear() {
        usesOwnConnection = false
        error = false

        peers.clear()
        peers.add(BLANK)

        YggdrasilPreferences.clear()
    }

    @Composable
    override fun Content() {
        Spacer(Modifier.height(24.dp))
        SettingsToggleRow(
            title = stringResource(Res.string.preference_use_yggdrasil),
            summary = stringResource(Res.string.preference_use_yggdrasil_summary),
            checked = usesOwnConnection,
            onCheckedChange = {
                usesOwnConnection = it
                error = false
            }
        )
        AnimatedVisibility(usesOwnConnection) {
            Column(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                peers.forEachIndexed { index, value ->
                    ProxyInputRow(
                        index = index,
                        value = value,
                        isError = error && !peerPattern.matches(value),
                        itemCount = peers.size,
                        maxItemCount = 3,
                        placeholder = stringResource(Res.string.text_yggdrasil_peer_format),
                        onAdd = { if (peers.size < 3) peers.add(BLANK) },
                        onRemove = { if (peers.size > 1) peers.removeLast() },
                        onValueChange = {
                            peers[index] = it.trim()
                            error = false
                        }
                    )
                }
            }
        }
    }
}
