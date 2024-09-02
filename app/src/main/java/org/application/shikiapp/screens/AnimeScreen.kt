package org.application.shikiapp.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AnimeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.CharacterScreenDestination
import com.ramcosta.composedestinations.generated.destinations.PersonScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import org.application.AnimeQuery
import org.application.AnimeQuery.Anime
import org.application.AnimeQuery.CharacterRole
import org.application.AnimeQuery.PersonRole
import org.application.AnimeQuery.ScoresStat
import org.application.AnimeQuery.Screenshot
import org.application.AnimeQuery.StatusesStat
import org.application.AnimeQuery.UserRate
import org.application.AnimeQuery.Video
import org.application.shikiapp.R
import org.application.shikiapp.R.drawable.vector_comments
import org.application.shikiapp.R.string.text_add_fav
import org.application.shikiapp.R.string.text_add_rate
import org.application.shikiapp.R.string.text_anime
import org.application.shikiapp.R.string.text_authors
import org.application.shikiapp.R.string.text_change
import org.application.shikiapp.R.string.text_change_rate
import org.application.shikiapp.R.string.text_characters
import org.application.shikiapp.R.string.text_episodes
import org.application.shikiapp.R.string.text_external_links
import org.application.shikiapp.R.string.text_genres
import org.application.shikiapp.R.string.text_image_of
import org.application.shikiapp.R.string.text_in_lists
import org.application.shikiapp.R.string.text_kind
import org.application.shikiapp.R.string.text_rate
import org.application.shikiapp.R.string.text_rating
import org.application.shikiapp.R.string.text_related
import org.application.shikiapp.R.string.text_remove_fav
import org.application.shikiapp.R.string.text_rewatches
import org.application.shikiapp.R.string.text_save
import org.application.shikiapp.R.string.text_score
import org.application.shikiapp.R.string.text_screenshots
import org.application.shikiapp.R.string.text_show_all_w
import org.application.shikiapp.R.string.text_similar
import org.application.shikiapp.R.string.text_statistics
import org.application.shikiapp.R.string.text_status
import org.application.shikiapp.R.string.text_studio
import org.application.shikiapp.R.string.text_user_rates
import org.application.shikiapp.R.string.text_video
import org.application.shikiapp.models.data.AnimeShort
import org.application.shikiapp.models.data.Comment
import org.application.shikiapp.models.data.ExternalLink
import org.application.shikiapp.models.views.AnimeState
import org.application.shikiapp.models.views.AnimeViewModel
import org.application.shikiapp.models.views.AnimeViewModel.Response.Error
import org.application.shikiapp.models.views.AnimeViewModel.Response.Loading
import org.application.shikiapp.models.views.AnimeViewModel.Response.Success
import org.application.shikiapp.models.views.CommentViewModel
import org.application.shikiapp.models.views.NewRate
import org.application.shikiapp.models.views.NewRateEvent.SetEpisodes
import org.application.shikiapp.models.views.NewRateEvent.SetRateId
import org.application.shikiapp.models.views.NewRateEvent.SetRewatches
import org.application.shikiapp.models.views.NewRateEvent.SetScore
import org.application.shikiapp.models.views.NewRateEvent.SetStatus
import org.application.shikiapp.models.views.NewRateEvent.SetText
import org.application.shikiapp.models.views.UserRateViewModel
import org.application.shikiapp.models.views.factory
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.EXTERNAL_LINK_KINDS
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.ROLES_RUSSIAN
import org.application.shikiapp.utils.SCORES
import org.application.shikiapp.utils.STATUSES
import org.application.shikiapp.utils.VideoKinds
import org.application.shikiapp.utils.WATCH_STATUSES
import org.application.shikiapp.utils.getImage
import org.application.shikiapp.utils.getKind
import org.application.shikiapp.utils.getRating
import org.application.shikiapp.utils.getStatus
import org.application.shikiapp.utils.getStudio
import org.application.shikiapp.utils.getWatchStatus
import com.ramcosta.composedestinations.navigation.DestinationsNavigator as Navigator


@Destination<RootGraph>
@Composable
fun AnimeScreen(id: String, navigator: Navigator) {
    val model = viewModel<AnimeViewModel>(factory = factory { AnimeViewModel(id) })
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    when (val data = response) {
        Error -> ErrorScreen(model::getAnime)
        Loading -> LoadingScreen()
        is Success -> AnimeView(model, state, data.anime, data.similar, data.links, data.favoured,  navigator)
    }
}

@Composable
private fun AnimeView(
    model: AnimeViewModel,
    state: AnimeState,
    anime: Anime,
    similar: List<AnimeShort>,
    links: List<ExternalLink>,
    favoured: Boolean,
    navigator: Navigator
) {
    val comments = anime.topic?.id?.let {
        viewModel<CommentViewModel>(factory = factory { CommentViewModel(it.toLong()) })
    }?.comments?.collectAsLazyPagingItems()

    Scaffold(topBar = topBar(model, comments, navigator)) { values ->
        LazyColumn(
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
            verticalArrangement = spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = anime.russian?.let { "$it / ${anime.name}" } ?: anime.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            item {
                Row(horizontalArrangement = spacedBy(8.dp)) {
                    Poster(anime.poster?.originalUrl)
                    ShortInfo(anime)
                }
            }

            anime.descriptionHtml?.let { if (fromHtml(it).isNotEmpty()) item { Description(it) } }
            anime.related?.let { if (it.isNotEmpty()) item { Related(model, it, navigator) } }
            anime.characterRoles?.let { if (it.isNotEmpty()) item { Characters(model, it, navigator) } }
            anime.personRoles?.let { if (it.isNotEmpty()) item { Authors(model, it, navigator) } }
            anime.screenshots.let { if (it.isNotEmpty()) item { Screenshots(model, it) } }
            anime.videos.let { if (it.isNotEmpty()) item { Video(model, it) } }
        }
    }

    when {
        state.showComments -> Comments(model::hideComments, comments!!, navigator)
        state.showSheet -> BottomSheet(model, state, anime.userRate, favoured)
        state.showRelated -> DialogRelated(model, anime.related!!, navigator)
        state.showCharacters -> DialogCharacters(model, state, anime.characterRoles!!, navigator)
        state.showAuthors -> DialogAuthors(model, state, anime.personRoles!!, navigator)
        state.showScreenshots -> DialogScreenshots(model, state, anime.screenshots)
        state.showScreenshot -> DialogScreenshot(model, state, anime.screenshots)
        state.showVideo -> DialogVideo(model, anime.videos)
        state.showRate -> CreateRate(model, anime)
        state.showSimilar -> DialogSimilar(model, state, similar, navigator)
        state.showStats -> Statistics(model, anime)
        state.showLinks -> LinksSheet(model, state, links)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun topBar(
    model: AnimeViewModel,
    comments: LazyPagingItems<Comment>?,
    navigator: DestinationsNavigator
): @Composable () -> Unit = {
    TopAppBar(
        title = { Text(stringResource(text_anime)) },
        navigationIcon = { NavigationIcon(navigator::popBackStack) },
        actions = {
            comments?.let { IconButton(model::showComments) { Icon(painterResource(vector_comments), null) } }
            IconButton(model::showSheet) { Icon(Icons.Outlined.MoreVert, null) }
        }
    )
}

@Composable
private fun ShortInfo(anime: Anime) {
    val name = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light)
    val info = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)

    fun getEpisodes(): String = when (anime.status?.rawValue) {
        STATUSES.keys.elementAt(2) -> "${anime.episodes} / ${anime.episodes}"
        else -> "${anime.episodesAired} / ${anime.episodes}"
    }

    Column(Modifier.height(300.dp), SpaceBetween) {
        Column {
            Text(stringResource(text_kind), style = name)
            Text(getKind(anime.kind?.rawValue), style = info)
        }
        Column {
            Text(stringResource(text_episodes), style = name)
            Text(getEpisodes(), style = info)
        }
        Column {
            Text(stringResource(text_status), style = name)
            Text(getStatus(anime.status?.rawValue), style = info)
        }
        anime.genres?.let { genres ->
            Column {
                Text(stringResource(text_genres), style = name)
                Row(Modifier.horizontalScroll(rememberScrollState()), spacedBy(6.dp)) {
                    genres.forEach { (russian) -> Text(russian, style = info) }
                }
            }
        }
        Column {
            Text(stringResource(text_studio), style = name)
            Text(getStudio(anime.studios), style = info)
        }
        Column {
            Text(stringResource(text_score), style = name)
            Row(horizontalArrangement = spacedBy(4.dp), verticalAlignment = CenterVertically) {
                Icon(Icons.Default.Star, null, Modifier.size(16.dp), Color(0xFFFFC319))
                Text(anime.score.toString(), style = info)
            }
        }
        Column {
            Text(stringResource(text_rating), style = name)
            Text(getRating(anime.rating?.rawValue), style = info)
        }
    }
}

@Composable
private fun Related(model: AnimeViewModel, list: List<AnimeQuery.Related>, navigator: Navigator) =
    Column(verticalArrangement = spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_related), Modifier.padding(bottom = 4.dp))
            TextButton(model::showRelated) { Text(stringResource(text_show_all_w)) }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), spacedBy(12.dp)) {
            list.take(4).forEach { (anime, manga) ->
                Column(
                    Modifier
                        .width(120.dp)
                        .clickable { anime?.let { navigator.navigate(AnimeScreenDestination(it.id)) } }) {
                    RoundedRelatedPoster(anime?.poster?.originalUrl ?: manga?.poster?.originalUrl)
                    RelatedText(anime?.russian ?: anime?.name ?: manga?.russian ?: manga!!.name)
                }
            }
        }
    }

@Composable
private fun Characters(model: AnimeViewModel, roles: List<CharacterRole>, navigator: Navigator) =
    Column(verticalArrangement = spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_characters))
            IconButton(model::showCharacters) { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), spacedBy(12.dp), CenterVertically) {
            roles.filter { it.rolesRu.contains("Main") }.forEach { role ->
                role.character.let { character ->
                    Column(
                        modifier = Modifier.clickable {
                            navigator.navigate(CharacterScreenDestination(character.id))
                        },
                        verticalArrangement = spacedBy(4.dp),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        CircleImage(character.poster?.originalUrl)
                        TextCircleImage(character.russian ?: character.name)
                    }
                }
            }
        }
    }

@Composable
private fun Authors(model: AnimeViewModel, roles: List<PersonRole>, navigator: Navigator) =
    Column(verticalArrangement = spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_authors))
            IconButton(model::showAuthors) { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), spacedBy(8.dp), CenterVertically) {
            roles.filter { role -> role.rolesRu.any { it in ROLES_RUSSIAN } }.forEach { role ->
                role.person.let { person ->
                    Column(
                        modifier = Modifier.clickable {
                            navigator.navigate(PersonScreenDestination(person.id.toLong()))
                        },
                        verticalArrangement = spacedBy(4.dp),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        CircleImage(person.poster?.originalUrl)
                        TextCircleImage(person.russian ?: person.name)
                    }
                }
            }
        }
    }

@Composable
private fun Screenshots(model: AnimeViewModel, screenshots: List<Screenshot>) =
    Column(verticalArrangement = spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_screenshots), Modifier.padding(bottom = 4.dp))
            IconButton(model::showScreenshots) { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), spacedBy(12.dp)) {
            screenshots.take(6).forEachIndexed { index, screenshot ->
                AsyncImage(
                    model = screenshot.originalUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(172.dp, 97.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { model.showScreenshot(index) }
                )
            }
        }
    }

@Composable
private fun Video(
    model: AnimeViewModel,
    video: List<Video>,
    handler: UriHandler = LocalUriHandler.current
) = Column(verticalArrangement = spacedBy(4.dp)) {
    Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
        ParagraphTitle(stringResource(text_video), Modifier.padding(bottom = 4.dp))
        IconButton(model::showVideo) { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) }
    }
    Row(Modifier.horizontalScroll(rememberScrollState()), spacedBy(12.dp)) {
        video.take(3).forEach { video ->
            AsyncImage(
                model = "https:${video.imageUrl}",
                contentDescription = null,
                modifier = Modifier
                    .size(172.dp, 130.dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable { handler.openUri(video.url) }
            )
        }
    }
}

// =========================================== Dialogs ===========================================

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DialogRelated(model: AnimeViewModel, list: List<AnimeQuery.Related>, navigator: Navigator) =
    Dialog(model::hideRelated, DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(text_related)) },
                    navigationIcon = { NavigationIcon(model::hideRelated) }
                )
            }
        ) { values ->
            LazyColumn(contentPadding = values) {
                items(list) { (anime, manga, relationText) ->
                    ListItem(
                        headlineContent = {
                            Text(
                                text = anime?.russian ?: anime?.name ?: manga?.russian ?: manga!!.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        modifier = Modifier.clickable {
                            anime?.let { navigator.navigate(AnimeScreenDestination(it.id)) }
                        },
                        leadingContent = {
                            AsyncImage(
                                model = anime?.poster?.originalUrl ?: manga?.poster?.originalUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp, 121.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .border(
                                        (0.5).dp,
                                        MaterialTheme.colorScheme.onSurface,
                                        MaterialTheme.shapes.medium
                                    ),
                                contentScale = ContentScale.Crop
                            )
                        },
                        supportingContent = { Text(relationText) }
                    )
                }
            }
        }
    }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DialogCharacters(
    model: AnimeViewModel,
    state: AnimeState,
    roles: List<CharacterRole>,
    navigator: Navigator
) = Dialog(model::hideCharacters, DialogProperties(usePlatformDefaultWidth = false)) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_characters)) },
                navigationIcon = { NavigationIcon(model::hideCharacters) }
            )
        }
    ) { values ->
        LazyColumn(
            contentPadding = PaddingValues(top = values.calculateTopPadding()),
            state = state.lazyCharacters
        ) {
            items(roles) { (_, character) ->
                OneLineImage(
                    name = character.russian ?: character.name,
                    link = character.poster?.originalUrl,
                    modifier = Modifier.clickable {
                        navigator.navigate(CharacterScreenDestination(character.id))
                    }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DialogAuthors(
    model: AnimeViewModel,
    state: AnimeState,
    roles: List<PersonRole>,
    navigator: Navigator
) = Dialog(model::hideAuthors, DialogProperties(usePlatformDefaultWidth = false)) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_authors)) },
                navigationIcon = { NavigationIcon(model::hideAuthors) }
            )
        }
    ) { values ->
        LazyColumn(
            contentPadding = PaddingValues(vertical = values.calculateTopPadding()),
            state = state.lazyAuthors
        ) {
            items(roles) { (_, person) ->
                OneLineImage(
                    name = person.russian ?: person.name,
                    link = person.poster?.originalUrl,
                    modifier = Modifier.clickable {
                        navigator.navigate(PersonScreenDestination(person.id.toLong()))
                    }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DialogScreenshots(model: AnimeViewModel, state: AnimeState, list: List<Screenshot>) =
    Dialog(model::hideScreenshots, DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(text_screenshots)) },
                    navigationIcon = { NavigationIcon(model::hideScreenshots) }
                )
            }
        ) { values ->
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(100.dp),
                contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
                verticalItemSpacing = 2.dp,
                horizontalArrangement = spacedBy(2.dp)
            ) {
                items(list.size) {
                    AsyncImage(
                        model = list[it].originalUrl,
                        contentDescription = null,
                        modifier = Modifier.clickable { model.showScreenshot(it) }
                    )
                }
            }
        }
    }.also { if (state.showScreenshot) DialogScreenshot(model, state, list) }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DialogScreenshot(model: AnimeViewModel, state: AnimeState, list: List<Screenshot>) {
    val pagerState = rememberPagerState(state.screenshot) { list.size }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collectLatest(model::setScreenshot)
    }

    Dialog(model::hideScreenshot, DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(text_image_of, state.screenshot + 1, list.size)) },
                    navigationIcon = { NavigationIcon(model::hideScreenshot) }
                )
            }
        ) { values ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = values
            ) { AsyncImage(list[it].originalUrl, null) }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
private fun DialogVideo(
    model: AnimeViewModel,
    list: List<Video>,
    handler: UriHandler = LocalUriHandler.current
) = Dialog(model::hideVideo, DialogProperties(usePlatformDefaultWidth = false)) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_video)) },
                navigationIcon = { NavigationIcon(model::hideVideo) }
            )
        }
    ) { values ->
        Column(
            modifier = Modifier
                .padding(8.dp, values.calculateTopPadding(), 8.dp, 0.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = spacedBy(12.dp)
        ) {
            VideoKinds.entries.forEach { entry ->
                ParagraphTitle(entry.title, Modifier.padding(bottom = 4.dp))
                FlowRow(Modifier.fillMaxWidth(), SpaceBetween, spacedBy(12.dp)) {
                    list.filter { it.kind.rawValue in entry.kinds }.sortedBy { it.name }
                        .forEach { video ->
                            Column(verticalArrangement = spacedBy(4.dp)) {
                                AsyncImage(
                                    model = "https:${video.imageUrl}",
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(172.dp, 130.dp)
                                        .clip(MaterialTheme.shapes.small)
                                        .clickable { handler.openUri(video.url) }
                                )
                                video.name?.let {
                                    Text(
                                        text = it,
                                        modifier = Modifier.width(172.dp),
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                            }
                        }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheet(model: AnimeViewModel, state: AnimeState, rate: UserRate?, star: Boolean) =
    ModalBottomSheet(model::hideSheet, sheetState = state.sheetBottom) {
        if (Preferences.isTokenExists()) {
            ListItem(
                headlineContent = { Text(stringResource(rate?.let { text_change_rate } ?: text_add_rate)) },
                modifier = Modifier.clickable { model.showRate() },
                leadingContent = { Icon(Icons.Outlined.Edit, null) }
            )
            ListItem(
                headlineContent = { Text(stringResource(if (star) text_remove_fav else text_add_fav)) },
                modifier = Modifier.clickable { model.changeFavourite(star) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = if (star) Color.Red else LocalContentColor.current
                    )
                }
            )
        }
        ListItem(
            headlineContent = { Text(stringResource(text_similar)) },
            modifier = Modifier.clickable { model.showSimilar() },
            leadingContent = { Icon(painterResource(R.drawable.vector_similar), null) }
        )
        ListItem(
            headlineContent = { Text(stringResource(text_statistics)) },
            modifier = Modifier.clickable { model.showStats() },
            leadingContent = { Icon(Icons.Outlined.Info, null) }
        )
        ListItem(
            headlineContent = { Text(stringResource(text_external_links)) },
            modifier = Modifier.clickable { model.showLinks() },
            leadingContent = {Icon(Icons.AutoMirrored.Outlined.List, null)}
        )
        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }

@Composable
private fun CreateRate(animeVM: AnimeViewModel, anime: Anime) {
    val model: UserRateViewModel = viewModel()
    val state by model.newRate.collectAsStateWithLifecycle()
    val exists by rememberSaveable { mutableStateOf(anime.userRate != null) }

    anime.userRate?.let { rate ->
        model.onEvent(SetRateId(rate.id))
        model.onEvent(SetStatus(WATCH_STATUSES.entries.first { it.key == rate.status.rawValue }))
        model.onEvent(SetScore(SCORES.entries.first { it.key == rate.score }))
        model.onEvent(SetEpisodes(rate.episodes.toString()))
        model.onEvent(SetRewatches(rate.rewatches.toString()))
    }

    AlertDialog(
        onDismissRequest = animeVM::hideRate,
        confirmButton = {
            TextButton(
                onClick = {
                    if (exists) model.updateRate(state.id) else model.createRate(anime.id)
                    animeVM.reload()
                },
                enabled = !state.status.isNullOrEmpty()
            ) { Text(stringResource(text_save)) }
        },
        dismissButton = {
            if (exists) TextButton({ model.deleteRate(state.id); animeVM.reload() })
            { Text("Удалить") }
        },
        title = {
            Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
                Text(stringResource(if (exists) text_change else text_rate))
                IconButton(animeVM::hideRate) { Icon(Icons.Outlined.Close, null) }
            }
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), spacedBy(16.dp)) {
                RateStatus(model, state)
                RateEpisodes(model, state)
                RateScore(model, state)
                RateRewatches(model, state)
                RateText(model, state)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogSimilar(
    model: AnimeViewModel,
    state: AnimeState,
    list: List<AnimeShort>,
    navigator: Navigator
) = Dialog(model::hideSimilar, DialogProperties(usePlatformDefaultWidth = false)) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_similar)) },
                navigationIcon = { NavigationIcon(model::hideSimilar) })
        }
    ) { values ->
        LazyColumn(
            state = state.lazySimilar,
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding(), 8.dp, 0.dp),
            verticalArrangement = spacedBy(8.dp)
        ) {
            items(list) { (id, name, russian, image) ->
                ListItem(
                    headlineContent = { Text(russian ?: name) },
                    modifier = Modifier.clickable {
                        navigator.navigate(AnimeScreenDestination(id.toString()))
                    },
                    leadingContent = {
                        AsyncImage(
                            model = getImage(image.original),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .border(
                                    (0.5).dp,
                                    MaterialTheme.colorScheme.onSurface,
                                    MaterialTheme.shapes.medium
                                )
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Statistics(model: AnimeViewModel, anime: Anime) =
    Dialog(model::hideStats, DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(text_anime)) },
                    navigationIcon = { NavigationIcon(model::hideStats) }
                )
            }
        ) { values ->
            LazyColumn(
                contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
                verticalArrangement = spacedBy(16.dp)
            ) {
                anime.scoresStats?.let { item { Scores(it) } }
                anime.statusesStats?.let { item { Statuses(it) } }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun LinksSheet(
    model: AnimeViewModel,
    state: AnimeState,
    list: List<ExternalLink>,
    handler: UriHandler = LocalUriHandler.current
) = ModalBottomSheet(model::hideLinks, sheetState = state.sheetLinks) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 4.dp),
        horizontalArrangement = SpaceBetween,
        verticalArrangement = spacedBy(8.dp)
    ) {
        list.forEach { link ->
            EXTERNAL_LINK_KINDS.entries.firstOrNull { it.key == link.kind }?.let {
                ElevatedCard(
                    onClick = { handler.openUri(link.url) },
                    modifier = Modifier.size(100.dp),
                    colors = CardDefaults.elevatedCardColors().copy(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    AsyncImage(
                        model = "https://www.google.com/s2/favicons?domain=${Uri.parse(link.url).host}&sz=64",
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .size(64.dp),
                        alignment = Center,
                        filterQuality = FilterQuality.High
                    )
                    Text(
                        text = it.value,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
}

@Composable
private fun Scores(scores: List<ScoresStat>) {
    val sum = scores.sumOf { it.count }

    ParagraphTitle(stringResource(text_user_rates), Modifier.padding(bottom = 4.dp))
    Column(verticalArrangement = spacedBy(8.dp)) {
        scores.forEach { (score, count) ->
            Row(Modifier.fillMaxWidth(), SpaceBetween) {
                Column(Modifier.fillMaxWidth(0.625f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(count.toFloat() / sum + 0.15f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = CenterEnd
                    ) {
                        Text(
                            text = count.toString(),
                            modifier = Modifier.padding(end = 4.dp),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
                Text(
                    text = score.toString(),
                    modifier = Modifier.padding(start = 8.dp),
                    overflow = TextOverflow.Visible,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun Statuses(statuses: List<StatusesStat>) {
    val sum = statuses.sumOf { it.count }

    ParagraphTitle(stringResource(text_in_lists), Modifier.padding(bottom = 4.dp))
    Column(verticalArrangement = spacedBy(8.dp)) {
        statuses.forEach { (status, count) ->
            Row(Modifier.fillMaxWidth(), SpaceBetween) {
                Column(Modifier.fillMaxWidth(0.625f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(count.toFloat() / sum + 0.165f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = CenterEnd
                    ) {
                        Text(
                            text = count.toString(),
                            modifier = Modifier.padding(end = 4.dp),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
                Text(
                    text = getWatchStatus(status.rawValue),
                    modifier = Modifier.padding(start = 8.dp),
                    overflow = TextOverflow.Visible,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RateStatus(viewModel: UserRateViewModel, state: NewRate) {
    var flag by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(flag, { flag = it }) {
        OutlinedTextField(
            value = state.statusName,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            label = { Text(stringResource(text_status)) },
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(flag) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(flag, { flag = false }) {
            WATCH_STATUSES.entries.forEach { entry ->
                DropdownMenuItem(
                    text = { Text(text = entry.value, style = MaterialTheme.typography.bodyLarge) },
                    onClick = { viewModel.onEvent(SetStatus(entry)); flag = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateScore(viewModel: UserRateViewModel, state: NewRate) {
    var flag by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(flag, { flag = it }) {
        OutlinedTextField(
            value = state.scoreName ?: BLANK,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            label = { Text(stringResource(text_score)) },
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(flag) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(flag, { flag = false }) {
            SCORES.entries.forEach { entry ->
                DropdownMenuItem(
                    text = { Text(text = entry.value, style = MaterialTheme.typography.bodyLarge) },
                    onClick = { viewModel.onEvent(SetScore(entry)); flag = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
fun RateEpisodes(viewModel: UserRateViewModel, state: NewRate) = OutlinedTextField(
    value = state.episodes ?: BLANK,
    onValueChange = { viewModel.onEvent(SetEpisodes(it)) },
    label = { Text(stringResource(text_episodes)) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
)

@Composable
fun RateRewatches(viewModel: UserRateViewModel, state: NewRate) = OutlinedTextField(
    value = state.rewatches ?: BLANK,
    onValueChange = { viewModel.onEvent(SetRewatches(it)) },
    label = { Text(stringResource(text_rewatches)) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
)

@Composable
fun RateText(viewModel: UserRateViewModel, state: NewRate) = OutlinedTextField(
    value = state.text ?: BLANK,
    onValueChange = { viewModel.onEvent(SetText(it)) },
    label = { Text(stringResource(R.string.text_comment)) }
)