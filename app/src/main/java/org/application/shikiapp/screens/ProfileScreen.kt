package org.application.shikiapp.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import org.application.shikiapp.R.string.text_login
import org.application.shikiapp.models.views.ProfileViewModel
import org.application.shikiapp.models.views.ProfileViewModel.LoginState.Logged
import org.application.shikiapp.models.views.ProfileViewModel.LoginState.Logging
import org.application.shikiapp.models.views.ProfileViewModel.LoginState.NoNetwork
import org.application.shikiapp.models.views.ProfileViewModel.LoginState.NotLogged
import org.application.shikiapp.network.client.AUTH_URL
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.CLIENT_ID
import org.application.shikiapp.utils.CODE
import org.application.shikiapp.utils.REDIRECT_URI
import org.application.shikiapp.utils.isDomainVerified

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    toAnime: (String) -> Unit,
    toManga: (String) -> Unit,
    toCharacter: (String) -> Unit,
    toPerson: (Long) -> Unit,
    toUser:(Long) -> Unit,
    toClub:(Long) ->Unit
) {
    val model = viewModel<ProfileViewModel>()
    val loginState by model.login.collectAsStateWithLifecycle()

    when (val data = loginState) {
        NotLogged -> LoginScreen()
        Logging -> LoadingScreen()
        NoNetwork -> ErrorScreen(model::getProfile)
        is Logged -> {
            val user = data.user
            val state by model.state.collectAsStateWithLifecycle()
            val comments = data.comments.collectAsLazyPagingItems()

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {},
                        navigationIcon = {
                            IconButton(model::signOut) {
                                Icon(Icons.AutoMirrored.Outlined.ExitToApp, null)
                            }
                        },
                        actions = {
                            IconButton(model::showSheet) { Icon(Icons.Outlined.MoreVert, null) }
                        }
                    )
                }
            ) { values ->
                LazyColumn(
                    contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
                    verticalArrangement = spacedBy(16.dp)
                ) {
                    item { UserBriefItem(user) }
                    item { BriefInfo(model::setMenu) }
                    item { UserStats(user.stats, user.id, toAnime, toManga) }

                    if (comments.itemCount > 0) comments(comments, toUser)
                }
            }

            when {
                state.showDialog -> {
                    val friends = model.friends.collectAsLazyPagingItems()
                    DialogItem(model, state, friends, data.clubs, toUser, toClub)
                }

                state.showFavourite -> DialogFavourites(
                    model::hideFavourite, model::setTab, state.tab, data.favourites, toAnime, toManga, toCharacter, toPerson
                )

                state.showHistory -> {
                    val history = model.history.collectAsLazyPagingItems()
                    DialogHistory(model::hideHistory, history, toAnime, toManga)
                }

                state.showSheet -> BottomSheet(
                    model::hideSheet, model::showFavourite, model::showHistory, state.sheetState
                )
            }
        }
    }
}

@Composable
private fun LoginScreen() {
    val context = LocalContext.current

    var selected by remember { mutableStateOf(context.isDomainVerified()) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        selected = context.isDomainVerified()
    }

    val uri = Uri.parse(AUTH_URL)
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
                                    Uri.parse("package:${context.packageName}")
                                )
                            )
                        }
                    ) { Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null) }
                }
            )
    }
}