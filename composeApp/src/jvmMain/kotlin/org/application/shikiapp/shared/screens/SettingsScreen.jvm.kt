package org.application.shikiapp.shared.screens

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable

actual fun LazyListScope.deeplinkSetting(onClick: () -> Unit) = Unit

@Composable
actual fun DeeplinkScreen(isVisible: Boolean, onBack: () -> Unit) = Unit