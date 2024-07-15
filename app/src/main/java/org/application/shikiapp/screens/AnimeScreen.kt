package org.application.shikiapp.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
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
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.CharacterScreenDestination
import com.ramcosta.composedestinations.generated.destinations.PersonScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.application.AnimeQuery.Anime
import org.application.AnimeQuery.CharacterRole
import org.application.AnimeQuery.Genre
import org.application.AnimeQuery.PersonRole
import org.application.AnimeQuery.ScoresStat
import org.application.AnimeQuery.Screenshot
import org.application.AnimeQuery.StatusesStat
import org.application.AnimeQuery.Studio
import org.application.AnimeQuery.Video
import org.application.shikiapp.R.string.text_anime
import org.application.shikiapp.R.string.text_authors
import org.application.shikiapp.R.string.text_cancel
import org.application.shikiapp.R.string.text_change
import org.application.shikiapp.R.string.text_characters
import org.application.shikiapp.R.string.text_episodes
import org.application.shikiapp.R.string.text_genres
import org.application.shikiapp.R.string.text_in_lists
import org.application.shikiapp.R.string.text_kind
import org.application.shikiapp.R.string.text_rate
import org.application.shikiapp.R.string.text_rating
import org.application.shikiapp.R.string.text_rewatches
import org.application.shikiapp.R.string.text_save
import org.application.shikiapp.R.string.text_score
import org.application.shikiapp.R.string.text_screenshots
import org.application.shikiapp.R.string.text_show_all_m
import org.application.shikiapp.R.string.text_show_all_w
import org.application.shikiapp.R.string.text_status
import org.application.shikiapp.R.string.text_studio
import org.application.shikiapp.R.string.text_title_english
import org.application.shikiapp.R.string.text_title_japanese
import org.application.shikiapp.R.string.text_title_synonyms
import org.application.shikiapp.R.string.text_titles
import org.application.shikiapp.R.string.text_user_rates
import org.application.shikiapp.R.string.text_video
import org.application.shikiapp.models.views.AnimeResponse
import org.application.shikiapp.models.views.AnimeState
import org.application.shikiapp.models.views.AnimeViewModel
import org.application.shikiapp.models.views.CommentViewModel
import org.application.shikiapp.models.views.NewRate
import org.application.shikiapp.models.views.NewRateEvent.SetEpisodes
import org.application.shikiapp.models.views.NewRateEvent.SetRateId
import org.application.shikiapp.models.views.NewRateEvent.SetRewatches
import org.application.shikiapp.models.views.NewRateEvent.SetScore
import org.application.shikiapp.models.views.NewRateEvent.SetStatus
import org.application.shikiapp.models.views.UserRateViewModel
import org.application.shikiapp.models.views.factory
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.ROLES_RUSSIAN
import org.application.shikiapp.utils.SCORES
import org.application.shikiapp.utils.STATUSES
import org.application.shikiapp.utils.VideoKinds
import org.application.shikiapp.utils.WATCH_STATUSES
import org.application.shikiapp.utils.getKind
import org.application.shikiapp.utils.getRating
import org.application.shikiapp.utils.getStatus
import org.application.shikiapp.utils.getWatchStatus


@Destination<RootGraph>
@Composable
fun AnimeScreen(id: String, navigator: DestinationsNavigator) {
    val model = viewModel<AnimeViewModel>(factory = factory { AnimeViewModel(id) })
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    when (val data = response) {
        is AnimeResponse.Error -> ErrorScreen(model.getAnime())
        is AnimeResponse.Loading -> LoadingScreen()
        is AnimeResponse.Success -> AnimeView(model, state, data.anime, data.favoured, navigator)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AnimeView(
    model: AnimeViewModel,
    state: AnimeState,
    anime: Anime,
    favoured: Boolean,
    navigator: DestinationsNavigator
) {
    val comments = anime.topic?.id?.let {
        viewModel<CommentViewModel>(factory = factory { CommentViewModel(it.toLong()) })
    }?.comments?.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_anime)) },
                navigationIcon = { NavigationIcon(navigator::popBackStack) },
                actions = {
                    IconButton(model::showFull) { Icon(Icons.Outlined.Info, null) }
                    if (Preferences.isTokenExists()) {
                        IconButton(model::showRate) { Icon(Icons.Default.Star, null) }
                        IconButton(if (favoured) model::deleteFavourite else model::addFavourite) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = if (favoured) Color.Red else LocalContentColor.current
                            )
                        }
                    }
                }
            )
        }
    ) { values ->
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
            anime.characterRoles?.let { item { Characters(model, state, it, navigator) } }
            anime.personRoles?.let { item { Authors(model, state, it, navigator) } }
            comments?.let { comments(it, navigator) }
        }
    }

    if (state.showFull) FullInfo(model, state, anime)
    if (state.showRate) CreateRate(model, anime)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FullInfo(model: AnimeViewModel, state: AnimeState, anime: Anime) =
    Dialog(model::hideFull, DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(text_anime)) },
                    navigationIcon = { NavigationIcon(model::hideFull) }
                )
            }
        ) { values ->
            LazyColumn(
                contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
                verticalArrangement = spacedBy(16.dp)
            ) {
                item { Titles(anime) }
                item { Genres(anime.genres) }
                item { Studio(anime.studios.first()) }
                anime.scoresStats?.let { item { Scores(it) } }
                anime.statusesStats?.let { item { Statuses(it) } }
                anime.screenshots.let { if (it.isNotEmpty()) item { Screenshots(model, state, it) } }
                anime.videos.let { if (it.isNotEmpty()) item { Video(model, state, it) } }
            }
        }
    }

@Composable
private fun Titles(anime: Anime) {
    Column {
        ParagraphTitle(stringResource(text_titles))
        Text(stringResource(text_title_japanese, anime.japanese.orEmpty()))
        Text(stringResource(text_title_english, anime.english.orEmpty()))
        Text(stringResource(text_title_synonyms, anime.synonyms.joinToString(", ")))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Genres(genres: List<Genre>?) {
    Column {
        ParagraphTitle(stringResource(text_genres))
        FlowRow(horizontalArrangement = spacedBy(8.dp)) {
            genres?.forEach { genre ->
                SuggestionChip({}, { Text(genre.russian) }, Modifier.wrapContentSize())
            }
        }
    }
}

@Composable
private fun Studio(studio: Studio) {
    ParagraphTitle(stringResource(text_studio))
    AsyncImage(studio.imageUrl, null)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Screenshots(model: AnimeViewModel, state: AnimeState, screenshots: List<Screenshot>) {
    Column(verticalArrangement = spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_screenshots), Modifier.padding(bottom = 4.dp))
            TextButton(model::showScreenshots) { Text(stringResource(text_show_all_w)) }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), spacedBy(12.dp)) {
            screenshots.take(6).forEachIndexed { index, screenshot ->
                AsyncImage(
                    model = screenshot.originalUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(172.dp, 93.dp)
                        .clickable { model.showScreenshot(index) }
                )
            }
        }
    }

    when {
        state.showScreenshot ->
            Dialog(model::hideScreenshot, DialogProperties(usePlatformDefaultWidth = false)) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {},
                            navigationIcon = { NavigationIcon(model::hideScreenshot) }
                        )
                    }
                ) { values ->
                    HorizontalPager(
                        state = rememberPagerState(state.screenshot) { screenshots.size },
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = values
                    ) { AsyncImage(screenshots[it].originalUrl, null) }
                }
            }

        state.showScreenshots ->
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
                        items(screenshots.size) {
                            AsyncImage(
                                model = screenshots[it].originalUrl,
                                contentDescription = null,
                                modifier = Modifier.clickable { model.showScreenshot(it) }
                            )
                        }
                    }
                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun Video(
    model: AnimeViewModel,
    state: AnimeState,
    video: List<Video>,
    handler: UriHandler = LocalUriHandler.current
) {
    Column(verticalArrangement = spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_video), Modifier.padding(bottom = 4.dp))
            TextButton(model::showVideo) { Text(stringResource(text_show_all_w)) }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), spacedBy(12.dp)) {
            video.take(3).forEach { video ->
                AsyncImage(
                    model = "https:${video.imageUrl}",
                    contentDescription = null,
                    modifier = Modifier
                        .size(172.dp, 130.dp)
                        .clickable { handler.openUri(video.url) }
                )
            }
        }
    }

    if (state.showVideo) {
        Dialog(model::hideVideo, DialogProperties(usePlatformDefaultWidth = false)) {
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
                            video.filter { it.kind.rawValue in entry.kinds }.sortedBy { it.name }
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
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Characters(
    model: AnimeViewModel,
    state: AnimeState,
    roles: List<CharacterRole>,
    navigator: DestinationsNavigator
) {
    Column(verticalArrangement = spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_characters))
            TextButton(model::showCharacters) { Text(stringResource(text_show_all_m)) }
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
                        CircleImage(character.poster?.originalUrl, 76.dp)
                        NameCircleImage(character.russian ?: character.name, 76.dp)
                    }
                }
            }
        }
    }

    if (state.showCharacters)
        Dialog(model::hideCharacters, DialogProperties(usePlatformDefaultWidth = false)) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(text_characters)) },
                        navigationIcon = { NavigationIcon(model::hideCharacters) }
                    )
                }
            ) { values ->
                LazyColumn(
                    contentPadding = PaddingValues(vertical = values.calculateTopPadding()),
                    state = state.characterLazyState
                ) {
                    items(roles) { (_, character) ->
                        SmallItem(
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Authors(
    model: AnimeViewModel,
    state: AnimeState,
    roles: List<PersonRole>,
    navigator: DestinationsNavigator
) {
    Column(verticalArrangement = spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_authors))
            TextButton(model::showAuthors) { Text(stringResource(text_show_all_m)) }
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
                        CircleImage(person.poster?.originalUrl, 76.dp)
                        NameCircleImage(person.russian ?: person.name)
                    }
                }
            }
        }
    }

    if (state.showAuthors)
        Dialog(model::hideAuthors, DialogProperties(usePlatformDefaultWidth = false)) {
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
                    state = state.authorsLazyState
                ) {
                    items(roles) { (_, person) ->
                        SmallItem(
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
        dismissButton = { TextButton(animeVM::hideRate) { Text(stringResource(text_cancel)) } },
        title = { Text(stringResource(if (exists) text_change else text_rate)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), spacedBy(16.dp)) {
                RateStatus(model, state)
                RateEpisodes(model, state)
                RateScore(model, state)
                RateRewatches(model, state)
            }
        }
    )
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
fun RateEpisodes(viewModel: UserRateViewModel, state: NewRate) {
    OutlinedTextField(
        value = state.episodes ?: BLANK,
        onValueChange = { viewModel.onEvent(SetEpisodes(it)) },
        label = { Text(stringResource(text_episodes)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun RateRewatches(viewModel: UserRateViewModel, state: NewRate) {
    OutlinedTextField(
        value = state.rewatches ?: BLANK,
        onValueChange = { viewModel.onEvent(SetRewatches(it)) },
        label = { Text(stringResource(text_rewatches)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}