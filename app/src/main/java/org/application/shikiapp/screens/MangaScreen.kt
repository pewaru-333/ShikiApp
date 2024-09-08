package org.application.shikiapp.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.MangaScreenDestination
import org.application.MangaQuery.Data.Manga
import org.application.shikiapp.R.drawable.vector_comments
import org.application.shikiapp.R.string.text_genres
import org.application.shikiapp.R.string.text_kind
import org.application.shikiapp.R.string.text_manga
import org.application.shikiapp.R.string.text_publisher
import org.application.shikiapp.R.string.text_rate_chapters
import org.application.shikiapp.R.string.text_score
import org.application.shikiapp.R.string.text_similar
import org.application.shikiapp.R.string.text_status
import org.application.shikiapp.R.string.text_volumes
import org.application.shikiapp.models.data.ExternalLink
import org.application.shikiapp.models.data.MangaShort
import org.application.shikiapp.models.views.CommentViewModel
import org.application.shikiapp.models.views.MangaState
import org.application.shikiapp.models.views.MangaViewModel
import org.application.shikiapp.models.views.MangaViewModel.Response.Error
import org.application.shikiapp.models.views.MangaViewModel.Response.Loading
import org.application.shikiapp.models.views.MangaViewModel.Response.Success
import org.application.shikiapp.models.views.factory
import org.application.shikiapp.utils.LINKED_TYPE
import org.application.shikiapp.utils.STATUSES_M
import org.application.shikiapp.utils.getImage
import org.application.shikiapp.utils.getKind
import org.application.shikiapp.utils.getPublisher
import org.application.shikiapp.utils.getStatusM
import com.ramcosta.composedestinations.navigation.DestinationsNavigator as Navigator

@Destination<RootGraph>
@Composable
fun MangaScreen(id: String, navigator: Navigator) {
    val model = viewModel<MangaViewModel>(factory = factory { MangaViewModel(id) })
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    when (val data = response) {
        Error -> ErrorScreen(model::getManga)
        Loading -> LoadingScreen()
        is Success -> MangaView(
            model, state, data.manga, data.similar, data.links, data.favoured, navigator
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MangaView(
    model: MangaViewModel,
    state: MangaState,
    manga: Manga,
    similar: List<MangaShort>,
    links: List<ExternalLink>,
    favoured: Boolean,
    navigator: Navigator
) {
    val comments = manga.topic?.id?.let {
        viewModel<CommentViewModel>(factory = factory { CommentViewModel(it.toLong()) })
    }?.comments?.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text(stringResource(text_manga))},
                navigationIcon = { NavigationIcon(navigator::popBackStack) },
                actions = {
                    comments?.let { IconButton(model::showComments) { Icon(
                        painterResource(vector_comments), null) } }
                    IconButton(model::showSheet) { Icon(Icons.Outlined.MoreVert, null) }
                }
            )
        }
    ) { values->
        LazyColumn(
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
            verticalArrangement = spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = manga.russian?.let { "$it / ${manga.name}" } ?: manga.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            item {
                Row(horizontalArrangement = spacedBy(8.dp)) {
                    Poster(manga.poster?.originalUrl)
                    ShortInfo(manga)
                }
            }

            manga.descriptionHtml?.let { if (fromHtml(it).isNotEmpty()) item { Description(it) } }
            manga.related?.let {
                if (it.isNotEmpty()) item { Related(model::showRelated, it, navigator) }
            }
            manga.characterRoles?.let {
                if (it.isNotEmpty()) item { Characters(model::showCharacters, it, navigator) }
            }
            manga.personRoles?.let {
                if (it.isNotEmpty()) item { Authors(model::showAuthors, it, navigator) }
            }
        }
    }

    when {
        state.showSheet -> BottomSheet(
            model::hideSheet, model::showRate, model::showSimilar, model::showStats,
            model::showLinks, model::changeFavourite, state.sheetBottom, manga.userRate, favoured
        )
        state.showComments -> Comments(model::hideComments, comments!!, navigator)
        state.showRelated -> DialogRelated(model::hideRelated, manga.related!!, navigator)
        state.showCharacters -> DialogCharacters(model::hideCharacters, state.lazyCharacters, manga.characterRoles!!, navigator)
        state.showAuthors -> DialogAuthors(model::hideAuthors, state.lazyAuthors, manga.personRoles!!, navigator)
        state.showRate -> CreateRate(model::hideRate, model::reload, LINKED_TYPE[1], manga.id, manga.userRate)
        state.showSimilar -> DialogSimilar(model::hideSimilar, state.lazySimilar, similar, navigator)
        state.showStats -> Statistics(model::hideStats, manga.scoresStats, manga.statusesStats, LINKED_TYPE[1])
        state.showLinks -> LinksSheet(model::hideLinks, state.sheetLinks, links)
    }
}

@Composable
private fun ShortInfo(manga: Manga) {
    val name = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light)
    val info = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)

    Column(Modifier.height(300.dp), SpaceBetween) {
        Column {
            Text(stringResource(text_kind), style = name)
            Text(getKind(manga.kind?.rawValue), style = info)
        }
        manga.status?.rawValue?.let {
            if (it != STATUSES_M.keys.elementAt(1)) {
                Column {
                    Text(stringResource(text_volumes), style = name)
                    Text(manga.volumes.toString(), style = info)
                }
                Column {
                    Text(stringResource(text_rate_chapters), style = name)
                    Text(manga.chapters.toString(), style = info)
                }
            }
        }
        Column {
            Text(stringResource(text_status), style = name)
            Text(getStatusM(manga.status?.rawValue), style = info)
        }
        manga.genres?.let { genres ->
            Column {
                Text(stringResource(text_genres), style = name)
                Row(Modifier.horizontalScroll(rememberScrollState()), spacedBy(6.dp)) {
                    genres.forEach { (russian) -> Text(russian, style = info) }
                }
            }
        }
        Column {
            Text(stringResource(text_publisher), style = name)
            Text(getPublisher(manga.publishers), style = info)
        }
        Column {
            Text(stringResource(text_score), style = name)
            Row(horizontalArrangement = spacedBy(4.dp), verticalAlignment = CenterVertically) {
                Icon(Icons.Default.Star, null, Modifier.size(16.dp), Color(0xFFFFC319))
                Text(manga.score.toString(), style = info)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogSimilar(
    hide: () -> Unit,
    state: LazyListState,
    list: List<MangaShort>,
    navigator: Navigator
) = Dialog(hide, DialogProperties(usePlatformDefaultWidth = false)) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_similar)) },
                navigationIcon = { NavigationIcon(hide) })
        }
    ) { values ->
        LazyColumn(
            state = state,
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding(), 8.dp, 0.dp),
            verticalArrangement = spacedBy(8.dp)
        ) {
            items(list) { (id, name, russian, image) ->
                ListItem(
                    headlineContent = { Text(russian ?: name) },
                    modifier = Modifier.clickable {
                        navigator.navigate(MangaScreenDestination(id.toString()))
                    },
                    leadingContent = {
                        AsyncImage(
                            model = getImage(image.original),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .border((0.5).dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
                        )
                    }
                )
            }
        }
    }
}