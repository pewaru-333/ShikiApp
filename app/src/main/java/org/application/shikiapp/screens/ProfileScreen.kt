package org.application.shikiapp.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AnimeRatesScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.application.shikiapp.R.string.text_anime_list
import org.application.shikiapp.R.string.text_profile
import org.application.shikiapp.R.string.text_show_all_s
import org.application.shikiapp.models.data.Club
import org.application.shikiapp.models.data.ShortInfo
import org.application.shikiapp.models.views.CommentViewModel
import org.application.shikiapp.models.views.LoginState
import org.application.shikiapp.models.views.ProfileMenus
import org.application.shikiapp.models.views.ProfileViewModel
import org.application.shikiapp.models.views.factory
import org.application.shikiapp.network.AUTH_URL
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.CLIENT_ID
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.REDIRECT_URI
import org.application.shikiapp.utils.getWatchStatus

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun ProfileScreen(navigator: DestinationsNavigator, context: Context = LocalContext.current) {
    val viewModel: ProfileViewModel = viewModel()
    val state by viewModel.login.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            val code = (context as Activity).intent.data?.getQueryParameter("code")
            if (code != null && Preferences.getUserId() == 0L) viewModel.login(code)
        }
    }

    when (val data = state) {
        is LoginState.NotLogged -> LoginPage()
        is LoginState.Logging -> LoadingScreen()
        is LoginState.Logged -> {
            val user = data.user
            val comments = viewModel<CommentViewModel>(factory = factory {
                CommentViewModel(user.id)
            }).comments.collectAsLazyPagingItems()

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {},
                        actions = {
                            IconButton(viewModel::signOut) { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) }
                        }
                    )
                }
            ) { paddingValues ->
                LazyColumn(
                    contentPadding = PaddingValues(8.dp, paddingValues.calculateTopPadding()),
                    verticalArrangement = spacedBy(16.dp)
                ) {
                    item {
                        ListItem(
                            headlineContent = {},
                            modifier = Modifier.offset((-16).dp, (-8).dp),
                            overlineContent = {
                                Text(
                                    text = user.nickname,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            leadingContent = {
                                AsyncImage(
                                    model = user.avatar,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, Color.Gray, CircleShape)
                                )
                            },
                            supportingContent = { Text("${user.lastOnline}") }
                        )
                    }

                    item { BriefInfo(viewModel, data.clubs, navigator) }
                    item {
                        user.stats?.let { stats ->
                            if (stats.statuses.anime.sumOf { it.size } > 0)
                                AnimeStats(user.id, stats.statuses.anime, navigator)
                        }
                    }

                    if (comments.itemCount > 0) comments(comments, navigator)
                }
            }
        }
    }
}

@Composable
private fun LoginPage(context: Context = LocalContext.current) {
    val uri = Uri.parse(AUTH_URL)
        .buildUpon()
        .appendQueryParameter("client_id", CLIENT_ID)
        .appendQueryParameter("redirect_uri", REDIRECT_URI)
        .appendQueryParameter("response_type", "code")
        .appendQueryParameter("scope", BLANK)
        .build()

    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Button({ (context.startActivity(Intent(Intent.ACTION_VIEW, uri))) }) { Text("Вход") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BriefInfo(model: ProfileViewModel, clubs: List<Club>, navigator: DestinationsNavigator) {
    val friends = model.friends.collectAsLazyPagingItems()
    val state by model.state.collectAsStateWithLifecycle()

    ParagraphTitle(stringResource(text_profile), Modifier.padding(bottom = 4.dp))
    Row(Modifier.fillMaxWidth(), spacedBy(8.dp)) {
        ProfileMenus.entries.forEach { entry ->
            ElevatedCard(
                onClick = { model.setMenu(entry.ordinal) },
                modifier = Modifier
                    .height(64.dp)
                    .weight(1f)
            ) {
                Row(Modifier.fillMaxSize(), Arrangement.Center, CenterVertically) {
                    Text(text = entry.title, style = MaterialTheme.typography.titleSmall)
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                }
            }
        }
    }

    if (state.show) Dialog(model::close, DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(model.getTitle()) },
                    navigationIcon = { NavigationIcon(model::close) }
                )
            }
        ) { values ->
            LazyColumn(contentPadding = PaddingValues(top = values.calculateTopPadding())) {
                when (state.menu) {
                    0 -> friends(friends, navigator)
                    1 -> clubs(clubs, navigator)
                }
            }
        }
    }
}

@Composable
fun AnimeStats(id: Long, stats: List<ShortInfo>, navigator: DestinationsNavigator) {
    val sum = stats.sumOf { it.size }.takeIf { it != 0L } ?: 1

    Column(verticalArrangement = spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_anime_list))
            TextButton({ navigator.navigate(AnimeRatesScreenDestination(id)) })
            { Text(stringResource(text_show_all_s)) }
        }
        stats.filter { it.size > 0 }.forEach { (_, _, name, size) ->
            Row(Modifier.fillMaxWidth(), SpaceBetween) {
                Column(Modifier.fillMaxWidth(0.625f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(size.toFloat() / sum + 0.15f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = CenterEnd
                    ) {
                        Text(
                            text = size.toString(),
                            modifier = Modifier.padding(end = 4.dp),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
                Text(
                    text = getWatchStatus(name),
                    modifier = Modifier.padding(end = 4.dp),
                    overflow = TextOverflow.Visible,
                    maxLines = 1
                )
            }
        }
    }
}