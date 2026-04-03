package org.application.shikiapp.shared.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import me.zhanghai.compose.preference.preference
import org.application.shikiapp.shared.ui.templates.AnimatedDialogScreen
import org.application.shikiapp.shared.ui.templates.VectorIcon
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.PREF_DEEP_LINK_SETTINGS
import org.application.shikiapp.shared.utils.extensions.getLinkDomains
import org.application.shikiapp.shared.utils.extensions.isLinkHandlingAllowed
import org.application.shikiapp.shared.utils.extensions.openAppLinksSettings
import org.application.shikiapp.shared.utils.ui.rememberWindowSize
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.preference_deep_link
import shikiapp.composeapp.generated.resources.text_app_links_domain_status
import shikiapp.composeapp.generated.resources.text_app_links_support
import shikiapp.composeapp.generated.resources.text_to_settings
import shikiapp.composeapp.generated.resources.text_turned_off
import shikiapp.composeapp.generated.resources.text_turned_on
import shikiapp.composeapp.generated.resources.vector_website

actual fun LazyListScope.deeplinkSetting(onClick: () -> Unit) = preference(
    key = PREF_DEEP_LINK_SETTINGS,
    title = { Text(stringResource(Res.string.preference_deep_link)) },
    onClick = onClick
)

@Composable
actual fun DeeplinkScreen(isVisible: Boolean, onBack: () -> Unit) =
    AnimatedDialogScreen(isVisible, BLANK, onBack) { values ->
        val context = LocalContext.current
        val isCompact = rememberWindowSize().isCompact

        var links by remember(context) { mutableStateOf(context.getLinkDomains()) }
        var isAllowed by remember(context) { mutableStateOf(context.isLinkHandlingAllowed()) }

        LifecycleResumeEffect(Unit) {
            links = context.getLinkDomains()
            isAllowed = context.isLinkHandlingAllowed()

            onPauseOrDispose { }
        }

        @Composable
        fun LocalChip(enabled: Boolean) {
            val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

            val greenContainer = if (isDark) Color(0xFF0D5322) else Color(0xFFC4EED0)
            val onGreenContainer = if (isDark) Color(0xFFC4EED0) else Color(0xFF043914)

            val label = stringResource(
                resource = if (enabled) Res.string.text_turned_on
                else Res.string.text_turned_off
            )

            val colors = SuggestionChipDefaults.suggestionChipColors(
                containerColor = if (enabled) greenContainer
                else MaterialTheme.colorScheme.errorContainer,
                labelColor = if (enabled) onGreenContainer
                else MaterialTheme.colorScheme.onErrorContainer,
            )

            SuggestionChip(
                border = null,
                onClick = {},
                label = { Text(label) },
                colors = colors
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = 800.dp)
                    .padding(horizontal = if (isCompact) 16.dp else 32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(80.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                    ) {
                        VectorIcon(
                            resId = Res.drawable.vector_website,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = stringResource(Res.string.text_app_links_support),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(Modifier.height(8.dp))

                    LocalChip(isAllowed)

                    Spacer(Modifier.height(32.dp))

                    Text(
                        text = stringResource(Res.string.text_app_links_domain_status),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(Modifier.height(12.dp))

                    Column(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                        links.entries.forEachIndexed { index, (key, value) ->
                            ListItem(
                                colors = ListItemDefaults.colors(
                                    containerColor = Color.Transparent
                                ),
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.medium)
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium),
                                headlineContent = {
                                    Text(
                                        text = key,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                leadingContent = {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                },
                                trailingContent = {
                                    LocalChip(value == 1)
                                }
                            )
                        }
                    }
                }

                Box(
                    contentAlignment = if (isCompact) Alignment.Center else Alignment.CenterEnd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    FilledTonalButton(
                        onClick = context::openAppLinksSettings,
                        modifier = if (isCompact) Modifier.fillMaxWidth() else Modifier,
                        content = { Text(stringResource(Res.string.text_to_settings)) }
                    )
                }
            }
        }
    }