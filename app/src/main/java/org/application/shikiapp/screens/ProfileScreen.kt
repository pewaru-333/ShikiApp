package org.application.shikiapp.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ktor.http.URLBuilder
import org.application.shikiapp.R
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
        is LoginResponse.NotLogged -> LoginScreen()
        is LoginResponse.Logging -> LoadingScreen()
        is LoginResponse.NetworkError -> ErrorScreen(model::loadData)
        is LoginResponse.Logged -> UserView(data.user, state, model::onEvent, onNavigate, model::signOut, visibility)

        else -> Unit
    }
}

@Composable
private fun LoginScreen() {
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 8.dp)
    ) {
        if (!verified) {
            Column(Modifier.align(Alignment.TopCenter), spacedBy(8.dp), CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.vector_glass_tick),
                    modifier = Modifier.size(56.dp),
                    contentDescription = null
                )
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
            }
        }

        Column(
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier
                .width(240.dp)
                .align(Alignment.Center)
        ) {
            if (verified) {
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
            } else {
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