@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.BaseDialogState
import org.application.shikiapp.shared.models.states.showDialogs
import org.application.shikiapp.shared.models.viewModels.ProfileViewModel
import org.application.shikiapp.shared.network.client.ApiRoutes
import org.application.shikiapp.shared.network.response.LoginResponse
import org.application.shikiapp.shared.ui.templates.Comments
import org.application.shikiapp.shared.ui.templates.VectorIcon
import org.application.shikiapp.shared.utils.navigation.LocalBarVisibility
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.rememberVerifiedDomain
import org.application.shikiapp.shared.utils.ui.rememberCommentListState
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
        is LoginResponse.NotLogged -> LoginScreen { model.onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.User.Settings)) }
        is LoginResponse.Logging -> LoadingScreen { model.onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.User.Settings)) }
        is LoginResponse.NetworkError -> ErrorScreen(model::loadData) { model.onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.User.Settings)) }
        is LoginResponse.Logged -> {
            UserView(data.user, state, model.mailManager, model::onEvent, onNavigate, model::onShowSignOut, barVisibility)

            val comments = data.user.comments.collectAsLazyPagingItems()
            val commentListState = rememberCommentListState(
                list = comments,
                onCommentEvent = model.commentEvent
            )
            Comments(
                state = commentListState,
                isVisible = state.dialogState is BaseDialogState.Comments,
                isSending = state.isSendingComment,
                onNavigate = onNavigate,
                onHide = { model.onEvent(ContentDetailEvent.ToggleDialog(null)) },
                onCreateComment = { text, isOfftopic ->
                    model.onEvent(ContentDetailEvent.CreateComment(text, isOfftopic))
                },
                onUpdateComment = { id, text, isOfftopicChanged ->
                    model.onEvent(ContentDetailEvent.UpdateComment(id, text, isOfftopicChanged))
                },
                onDeleteComment = { id ->
                    model.onEvent(ContentDetailEvent.DeleteComment(id))
                }
            )
        }

        else -> Unit
    }

    LaunchedEffect(state.dialogState, state.showDialogs) {
        if (state.dialogState != null && state.dialogState != BaseDialogState.User.Logout || state.showDialogs) {
            barVisibility.hide()
        } else {
            barVisibility.show()
        }
    }

    SettingsScreen(
        isVisible = state.dialogState is BaseDialogState.User.Settings,
        onBack = { model.onEvent(ContentDetailEvent.ToggleDialog(null)) }
    )

    if (state.dialogState is BaseDialogState.User.Logout) {
        DialogLogout(
            onConfirm = model::signOut,
            onDismiss = { model.onEvent(ContentDetailEvent.ToggleDialog(null)) }
        )
    }
}

@Composable
private fun LoginScreen(onClick: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val domainHelper = rememberVerifiedDomain()

    Box(Modifier.fillMaxSize()) {
        IconButtonSettings(onClick)

        if (domainHelper.isVerified) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(240.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { uriHandler.openUri(ApiRoutes.authUri) }
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
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
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
private fun LoadingScreen(onClick: () -> Unit) = Box(Modifier.fillMaxSize()) {
    IconButtonSettings(onClick)

    CircularProgressIndicator(Modifier.align(Alignment.Center))
}

@Composable
private fun ErrorScreen(onReload: () -> Unit, onClick: () -> Unit) = Box(Modifier.fillMaxSize()) {
    IconButtonSettings(onClick)

    FilledTonalButton(
        onClick = onReload,
        modifier = Modifier.align(Alignment.Center),
        content = { Text(stringResource(Res.string.text_repeat_the_loading)) }
    )
}

@Composable
private fun BoxScope.IconButtonSettings(onClick: () -> Unit) = IconButton(
    content = { VectorIcon(Res.drawable.vector_settings) },
    onClick = onClick,
    modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(8.dp)
)

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