package org.application.shikiapp.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.annotation.parameters.DeepLink
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.application.shikiapp.R.string.text_login
import org.application.shikiapp.models.views.CommentViewModel
import org.application.shikiapp.models.views.ProfileViewModel
import org.application.shikiapp.models.views.ProfileViewModel.LoginState.Logged
import org.application.shikiapp.models.views.ProfileViewModel.LoginState.Logging
import org.application.shikiapp.models.views.ProfileViewModel.LoginState.NoNetwork
import org.application.shikiapp.models.views.ProfileViewModel.LoginState.NotLogged
import org.application.shikiapp.models.views.factory
import org.application.shikiapp.network.AUTH_URL
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.CLIENT_ID
import org.application.shikiapp.utils.CODE
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.REDIRECT_URI

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(deepLinks = [DeepLink(uriPattern = REDIRECT_URI)])
@Composable
fun ProfileScreen(navigator: DestinationsNavigator, context: Context = LocalContext.current) {
    val model = viewModel<ProfileViewModel>()
    val loginState by model.login.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(lifecycleState) {
       if (lifecycleState == Lifecycle.State.RESUMED) {
           val code = (context as Activity).intent.data?.getQueryParameter(CODE)
           if (code != null && Preferences.getUserId() == 0L) model.login(code)
        }
    }

    when (val data = loginState) {
        NotLogged -> LoginScreen(context)
        Logging -> LoadingScreen()
        NoNetwork -> ErrorScreen(model::getProfile)
        is Logged -> {
            val user = data.user
            val state by model.state.collectAsStateWithLifecycle()
            val comments = viewModel<CommentViewModel>(factory = factory {
                CommentViewModel(user.id)
            }).comments.collectAsLazyPagingItems()

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
                    item { UserStats(user.stats, user.id, navigator) }

                    if (comments.itemCount > 0) comments(comments, navigator)
                }
            }

            when {
                state.showDialog -> {
                    val friends = model.friends.collectAsLazyPagingItems()
                    DialogItem(model, state, friends, data.clubs, navigator)
                }

                state.showFavourite -> DialogFavourites(
                    model::hideFavourite, model::setTab, state.tab, data.favourites, navigator
                )

                state.showHistory -> {
                    val history = model.history.collectAsLazyPagingItems()
                    DialogHistory(model::hideHistory, history, navigator)
                }

                state.showSheet -> BottomSheet(
                    model::hideSheet, model::showFavourite, model::showHistory, state.sheetState
                )
            }
        }
    }
}

@Composable
private fun LoginScreen(context: Context) {
    val uri = Uri.parse(AUTH_URL)
        .buildUpon()
        .appendQueryParameter("client_id", CLIENT_ID)
        .appendQueryParameter("redirect_uri", REDIRECT_URI)
        .appendQueryParameter("response_type", CODE)
        .appendQueryParameter("scope", BLANK)
        .build()

    Column(Modifier.fillMaxSize(), spacedBy(4.dp, CenterVertically), CenterHorizontally) {
        Button({ context.startActivity(Intent(Intent.ACTION_VIEW, uri)) })
        { Text(stringResource(text_login)) }
        ListItem(
            headlineContent = {},
            supportingContent = { Text("Для поддержки входа добавьте поддерживаемые ссылки в настройках") },
            trailingContent = {
                IconButton(
                    onClick = {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:${context.packageName}")
                            )
                        )
                    }
                ) { Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null) }
            }
        )
    }
}