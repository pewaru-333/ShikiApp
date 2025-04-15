package org.application.shikiapp.screens

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.application.shikiapp.R.string.text_login
import org.application.shikiapp.models.viewModels.ProfileViewModel
import org.application.shikiapp.network.LoginResponse
import org.application.shikiapp.network.client.AUTH_URL
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.CLIENT_ID
import org.application.shikiapp.utils.CODE
import org.application.shikiapp.utils.REDIRECT_URI
import org.application.shikiapp.utils.extensions.NavigationBarVisibility
import org.application.shikiapp.utils.isDomainVerified
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

    var selected by remember { mutableStateOf(context.isDomainVerified()) }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            selected = context.isDomainVerified()
        }

    val uri = AUTH_URL.toUri()
        .buildUpon()
        .appendQueryParameter("client_id", CLIENT_ID)
        .appendQueryParameter("redirect_uri", REDIRECT_URI)
        .appendQueryParameter("response_type", CODE)
        .appendQueryParameter("scope", BLANK)
        .build()

    Column(Modifier.fillMaxSize(), spacedBy(4.dp, CenterVertically), CenterHorizontally) {
        Button(
            enabled = selected,
            onClick = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, uri).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
            }
        )
        { Text(stringResource(text_login)) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ListItem(
                headlineContent = {},
                supportingContent = { Text("Для поддержки входа добавьте поддерживаемые ссылки в настройках") },
                trailingContent = {
                    IconButton(
                        onClick = {
                            launcher.launch(
                                Intent(
                                    Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                                    "package:${context.packageName}".toUri()
                                )
                            )
                        }
                    ) { Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null) }
                }
            )
    }
}