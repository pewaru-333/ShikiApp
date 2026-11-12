@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.shared.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
import org.application.shikiapp.shared.di.AppServices
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.BaseDialogState
import org.application.shikiapp.shared.models.states.showDialogs
import org.application.shikiapp.shared.models.viewModels.ProfileViewModel
import org.application.shikiapp.shared.network.client.Network
import org.application.shikiapp.shared.network.response.LoginResponse
import org.application.shikiapp.shared.network.response.Response
import org.application.shikiapp.shared.ui.templates.Comments
import org.application.shikiapp.shared.ui.templates.VectorIcon
import org.application.shikiapp.shared.ui.theme.Icons
import org.application.shikiapp.shared.utils.navigation.LocalBarVisibility
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.rememberToastState
import org.application.shikiapp.shared.utils.ui.rememberAuthLauncher
import org.application.shikiapp.shared.utils.ui.rememberCommentListState
import org.application.shikiapp.shared.utils.ui.rememberWindowSize
import org.application.shikiapp.shared.utils.viewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.*

@Composable
fun ProfileScreen(onNavigate: (Screen) -> Unit) {
    val barVisibility = LocalBarVisibility.current

    val model = viewModel(::ProfileViewModel)
    val loginState by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    when (val data = loginState) {
        is LoginResponse.NotLogged -> LoginScreen { model.onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.User.Settings)) }
        LoginResponse.Logging, Response.Loading -> LoadingScreen { model.onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.User.Settings)) }
        is LoginResponse.Error -> {
            if (data.canRetry) {
                ErrorScreen(data.message, model::loadData) { model.onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.User.Settings)) }
            } else {
                LoginScreen(data.message) { model.onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.User.Settings)) }
            }
        }
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

        else -> ErrorScreen(Res.string.text_error_profile, model::loadData) { model.onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.User.Settings)) }
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
private fun LoginScreen(message: StringResource? = null, onClick: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val authLauncher = rememberAuthLauncher(uriHandler)
    val toast = rememberToastState()
    val isCompact = rememberWindowSize().isCompact
    val isResolving by Network.baseUrlResolver.isResolving.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        IconButtonSettings(onClick)

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .width(if (isCompact) 240.dp else 320.dp)
        ) {
            if (message != null) {
                Text(
                    text = stringResource(message),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = authLauncher.isAvailable && !authLauncher.isLaunching && !isResolving,
                onClick = {
                    try {
                        authLauncher.launch()
                    } catch (_: Exception) {
                        toast.onShow(Res.string.text_error_open_link)
                    }
                }
            ) {
                Text(
                    text = stringResource(Res.string.text_login),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                if (isResolving || authLauncher.isLaunching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = LocalContentColor.current,
                        strokeWidth = 2.dp
                    )
                } else VectorIcon(Icons.KeyboardArrowRight)
            }

            val loginHint = when {
                !authLauncher.isAvailable -> AppServices.networkExtension?.authUnavailableMessage
                !authLauncher.usesWebView -> Res.string.text_forward_to_browser
                else -> null
            }
            if (loginHint != null) {
                Text(
                    text = stringResource(loginHint),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen(onClick: () -> Unit) =
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        IconButtonSettings(onClick)

        LoadingIndicator(Modifier.align(Alignment.Center))
    }

@Composable
private fun ErrorScreen(message: StringResource, onReload: () -> Unit, onClick: () -> Unit) =
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        IconButtonSettings(onClick)

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(message),
                textAlign = TextAlign.Center
            )
            FilledTonalButton(
                onClick = onReload,
                content = { Text(stringResource(Res.string.text_repeat_the_loading)) }
            )
        }
    }

@Composable
private fun BoxScope.IconButtonSettings(onClick: () -> Unit) = IconButton(
    content = { VectorIcon(Icons.Settings) },
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