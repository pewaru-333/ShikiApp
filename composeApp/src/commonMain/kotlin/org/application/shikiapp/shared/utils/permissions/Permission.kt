package org.application.shikiapp.shared.utils.permissions

import androidx.compose.runtime.Composable

@Composable
expect fun rememberPermissionState(permission: String): PermissionState