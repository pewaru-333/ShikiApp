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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import org.application.shikiapp.models.viewModels.ProfileViewModel
import org.application.shikiapp.network.response.LoginResponse
import org.application.shikiapp.utils.AUTH_SCOPES
import org.application.shikiapp.utils.AUTH_URL
import org.application.shikiapp.utils.CLIENT_ID
import org.application.shikiapp.utils.CODE
import org.application.shikiapp.utils.REDIRECT_URI
import org.application.shikiapp.utils.extensions.NavigationBarVisibility
import org.application.shikiapp.utils.extensions.appLinksSettings
import org.application.shikiapp.utils.extensions.isDomainVerified
import org.application.shikiapp.utils.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onNavigate: (Screen) -> Unit, visibility: NavigationBarVisibility) {
    val model = viewModel<ProfileViewModel>()
    val loginState by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    when (val data = loginState) {
        is LoginResponse.NotLogged -> LoginScreen { model.onEvent(ContentDetailEvent.User.ShowSettings) }
        is LoginResponse.Logging -> LoadingScreen()
        is LoginResponse.NetworkError -> ErrorScreen(model::loadData) { model.onEvent(ContentDetailEvent.User.ShowSettings) }
        is LoginResponse.Logged -> UserView(data.user, state, model::onEvent, onNavigate, model::signOut, visibility)

        else -> Unit
    }

    SettingsScreen(
        visible = state.showSettings,
        onBack = { model.onEvent(ContentDetailEvent.User.ShowSettings) }
    )

    LaunchedEffect(state.showSettings) {
        visibility.toggle(state.showSettings)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
                    IconButton(openSettings) { Icon(Icons.Outlined.Settings, null) }
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
                        Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null)
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
                    Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null)
                }
            }
        }
    }
}

@Composable
private fun ErrorScreen(reload: () -> Unit, openSettings: () -> Unit) =
    Box(Modifier.fillMaxSize()) {
        IconButton(
            onClick = openSettings,
            modifier = Modifier.align(Alignment.TopEnd),
            content = { Icon(Icons.Outlined.Settings, null) }
        )
        FilledTonalButton(
            onClick = reload,
            modifier = Modifier.align(Alignment.Center),
            content = { Text("Повторить загрузку") }
        )
    }