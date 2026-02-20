@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ktor.http.URLBuilder
import org.application.shikiapp.R
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.UserDialogState
import org.application.shikiapp.models.states.showDialogs
import org.application.shikiapp.models.viewModels.ProfileViewModel
import org.application.shikiapp.network.response.LoginResponse
import org.application.shikiapp.ui.templates.VectorIcon
import org.application.shikiapp.utils.AUTH_SCOPES
import org.application.shikiapp.utils.AUTH_URL
import org.application.shikiapp.utils.CLIENT_ID
import org.application.shikiapp.utils.CODE
import org.application.shikiapp.utils.REDIRECT_URI
import org.application.shikiapp.utils.extensions.appLinksSettings
import org.application.shikiapp.utils.extensions.isDomainVerified
import org.application.shikiapp.utils.navigation.LocalBarVisibility
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun ProfileScreen(onNavigate: (Screen) -> Unit) {
    val barVisibility = LocalBarVisibility.current

    val model = viewModel<ProfileViewModel>()
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
        visible = state.dialogState is UserDialogState.Settings,
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
    val context = LocalContext.current

    var verified by remember { mutableStateOf(context.isDomainVerified()) }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            verified = context.isDomainVerified()
        }

    val uri = URLBuilder(AUTH_URL).apply {
        encodedParameters.apply {
            append("client_id", CLIENT_ID)
            append("redirect_uri", REDIRECT_URI)
            append("response_type", CODE)
            append("scope", AUTH_SCOPES.joinToString("+", transform = String::lowercase))
        }
    }.buildString().toUri()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(openSettings) { VectorIcon(R.drawable.vector_settings) }
                }
            )
        }
    ) { values ->
        if (verified) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, values.calculateTopPadding())
            ) {
                Column(Modifier.width(240.dp)) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, uri).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                            )
                        }
                    ) {
                        Text(stringResource(R.string.text_login))
                        VectorIcon(R.drawable.vector_keyboard_arrow_right)
                    }

                    Text(
                        text = stringResource(R.string.text_forward_to_browser),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        } else {
            Column(Modifier.fillMaxSize(), Arrangement.spacedBy(8.dp, Alignment.CenterVertically), Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.text_pay_attention),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = stringResource(R.string.text_add_app_links),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                FilledTonalButton(
                    onClick = {
                        launcher.launch(context.appLinksSettings())
                    }
                ) {
                    Text(stringResource(R.string.text_to_settings))
                    VectorIcon(R.drawable.vector_keyboard_arrow_right)
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
                    content = { VectorIcon(R.drawable.vector_settings) }
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
                    content = { VectorIcon(R.drawable.vector_settings) }
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
                content = { Text(stringResource(R.string.text_repeat_the_loading)) }
            )
        }
    )
}

@Composable
private fun DialogLogout(onDismiss: () -> Unit, onConfirm: () -> Unit) =
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = { TextButton(onDismiss) { Text(stringResource(R.string.text_dismiss)) } },
        confirmButton = { TextButton(onConfirm) { Text(stringResource(R.string.text_confirm)) } },
        text = {
            Text(
                text = stringResource(R.string.text_sure_to_logout),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    )