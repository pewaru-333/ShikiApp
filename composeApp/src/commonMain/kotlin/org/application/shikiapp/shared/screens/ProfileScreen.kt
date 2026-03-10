@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.ktor.http.URLBuilder
import org.application.shikiapp.shared.di.AppConfig
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.UserDialogState
import org.application.shikiapp.shared.models.states.showDialogs
import org.application.shikiapp.shared.models.viewModels.ProfileViewModel
import org.application.shikiapp.shared.network.response.LoginResponse
import org.application.shikiapp.shared.ui.templates.VectorIcon
import org.application.shikiapp.shared.utils.AUTH_URL
import org.application.shikiapp.shared.utils.CLIENT_ID
import org.application.shikiapp.shared.utils.CODE
import org.application.shikiapp.shared.utils.REDIRECT_URI
import org.application.shikiapp.shared.utils.navigation.LocalBarVisibility
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.rememberVerifiedDomain
import org.application.shikiapp.shared.utils.viewModel
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_add_app_links
import shikiapp.composeapp.generated.resources.text_confirm
import shikiapp.composeapp.generated.resources.text_dismiss
import shikiapp.composeapp.generated.resources.text_forward_to_browser
import shikiapp.composeapp.generated.resources.text_login
import shikiapp.composeapp.generated.resources.text_pay_attention
import shikiapp.composeapp.generated.resources.text_repeat_the_loading
import shikiapp.composeapp.generated.resources.text_sure_to_logout
import shikiapp.composeapp.generated.resources.text_to_settings
import shikiapp.composeapp.generated.resources.vector_keyboard_arrow_right
import shikiapp.composeapp.generated.resources.vector_settings

@Composable
fun ProfileScreen(onNavigate: (Screen) -> Unit) {
    val barVisibility = LocalBarVisibility.current

    val model = viewModel(::ProfileViewModel)
    val loginState by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    when (val data = loginState) {
        is LoginResponse.NotLogged -> LoginScreen { model.onEvent(ContentDetailEvent.User.ToggleDialog(UserDialogState.Settings)) }
        is LoginResponse.Logging -> LoadingScreen { model.onEvent(ContentDetailEvent.User.ToggleDialog(UserDialogState.Settings)) }
        is LoginResponse.NetworkError -> ErrorScreen(model::loadData) { model.onEvent(ContentDetailEvent.User.ToggleDialog(UserDialogState.Settings)) }
        is LoginResponse.Logged -> UserView(data.user, state, model.mailManager, model::onEvent, onNavigate, model::onShowSignOut, barVisibility)

        else -> Unit
    }

    LaunchedEffect(state.dialogState, state.menu, state.showDialogs) {
        if (state.dialogState != null && state.dialogState != UserDialogState.Logout || state.menu != null || state.showDialogs) {
            barVisibility.hide()
        } else {
            barVisibility.show()
        }
    }

    SettingsScreen(
        isVisible = state.dialogState is UserDialogState.Settings,
        onBack = { model.onEvent(ContentDetailEvent.User.ToggleDialog(UserDialogState.Settings)) }
    )

    if (state.dialogState is UserDialogState.Logout) {
        DialogLogout(
            onConfirm = model::signOut,
            onDismiss = { model.onEvent(ContentDetailEvent.User.ToggleDialog(null)) }
        )
    }
}

@Composable
private fun LoginScreen(openSettings: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val domainHelper = rememberVerifiedDomain()

    val uri = URLBuilder(AUTH_URL).apply {
        encodedParameters.apply {
            append("client_id", CLIENT_ID)
            append("redirect_uri", REDIRECT_URI)
            append("response_type", CODE)
            append("scope", AppConfig.authScopes.joinToString("+", transform = String::lowercase))
        }
    }.buildString()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(openSettings) { VectorIcon(Res.drawable.vector_settings) }
                }
            )
        }
    ) { values ->
        if (domainHelper.isVerified) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, values.calculateTopPadding())
            ) {
                Column(Modifier.width(240.dp)) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { uriHandler.openUri(uri) }
                    ) {
                        Text(stringResource(Res.string.text_login))
                        VectorIcon(Res.drawable.vector_keyboard_arrow_right)
                    }

                    Text(
                        text = stringResource(Res.string.text_forward_to_browser),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        } else {
            Column(Modifier.fillMaxSize(), Arrangement.spacedBy(8.dp, Alignment.CenterVertically), Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(Res.string.text_pay_attention),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = stringResource(Res.string.text_add_app_links),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                FilledTonalButton(domainHelper::onSettingsLaunch) {
                    Text(stringResource(Res.string.text_to_settings))
                    VectorIcon(Res.drawable.vector_keyboard_arrow_right)
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen(openSettings: () -> Unit) = Scaffold(
    topBar = {
        TopAppBar(
            title = {},
            actions = {
                IconButton(
                    onClick = openSettings,
                    content = { VectorIcon(Res.drawable.vector_settings) }
                )
            }
        )
    }
) { values ->
    Box(
        content = { CircularProgressIndicator() },
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(values)
    )
}

@Composable
private fun ErrorScreen(reload: () -> Unit, openSettings: () -> Unit) = Scaffold(
    topBar = {
        TopAppBar(
            title = {},
            actions = {
                IconButton(
                    onClick = openSettings,
                    content = { VectorIcon(Res.drawable.vector_settings) }
                )
            }
        )
    }
) { values ->
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(values),
        content = {
            FilledTonalButton(
                onClick = reload,
                content = { Text(stringResource(Res.string.text_repeat_the_loading)) }
            )
        }
    )
}

@Composable
private fun DialogLogout(onDismiss: () -> Unit, onConfirm: () -> Unit) =
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = { TextButton(onDismiss) { Text(stringResource(Res.string.text_dismiss)) } },
        confirmButton = { TextButton(onConfirm) { Text(stringResource(Res.string.text_confirm)) } },
        text = {
            Text(
                text = stringResource(Res.string.text_sure_to_logout),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    )