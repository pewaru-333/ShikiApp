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
import org.application.MangaQuery.Data.Manga
import org.application.shikiapp.R.drawable.vector_comments
import org.application.shikiapp.R.string.text_genres
import org.application.shikiapp.R.string.text_kind
import org.application.shikiapp.R.string.text_manga
import org.application.shikiapp.R.string.text_publisher
import org.application.shikiapp.R.string.text_ranobe
import org.application.shikiapp.R.string.text_rate_chapters
import org.application.shikiapp.R.string.text_score
import org.application.shikiapp.R.string.text_similar
import org.application.shikiapp.R.string.text_status
import org.application.shikiapp.R.string.text_volumes
import org.application.shikiapp.models.data.MangaShort
import org.application.shikiapp.models.views.MangaState
import org.application.shikiapp.models.views.MangaViewModel
import org.application.shikiapp.models.views.MangaViewModel.Response.Error
import org.application.shikiapp.models.views.MangaViewModel.Response.Loading
import org.application.shikiapp.models.views.MangaViewModel.Response.Success
import org.application.shikiapp.utils.LINKED_TYPE
import org.application.shikiapp.utils.STATUSES_M
import org.application.shikiapp.utils.getImage
import org.application.shikiapp.utils.getKind
import org.application.shikiapp.utils.getPublisher
import org.application.shikiapp.utils.getStatusM
import org.application.type.MangaKindEnum.light_novel
import org.application.type.MangaKindEnum.novel

@Composable
fun MangaScreen(
    toAnime: (String) -> Unit,
    toManga: (String) -> Unit,
    toCharacter: (String) -> Unit,
    toPerson: (Long) -> Unit,
    toUser: (Long) -> Unit,
    back: () -> Unit
) {
    val model = viewModel<MangaViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    when (val data = response) {
        Error -> ErrorScreen(model::getManga)
        Loading -> LoadingScreen()
        is Success -> MangaView(
            model = model,
            state = state,
            data =data,
            toAnime = toAnime,
            toManga = toManga,
            toCharacter = toCharacter,
            toPerson = toPerson,
            toUser = toUser,
            back = back
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MangaView(
    model: MangaViewModel,
    state: MangaState,
    data: Success,
    toAnime: (String) -> Unit,
    toManga: (String) -> Unit,
    toCharacter: (String) -> Unit,
    toPerson: (Long) -> Unit,
    toUser: (Long) -> Unit,
    back: () -> Unit
) {
    val (manga, similar, links, _, favoured) = data
    val comments = data.comments.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (manga.kind in listOf(light_novel, novel)) text_ranobe
                            else text_manga
                        )
                    )
                },
                navigationIcon = { NavigationIcon(back) },
                actions = {
                    if (comments.itemCount > 0)
                        IconButton(model::showComments) { Icon(painterResource(vector_comments), null) }
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
                if (it.isNotEmpty()) item { Related(model::showRelated, it, toAnime, toManga) }
            }
            manga.characterRoles?.let {
                if (it.isNotEmpty()) item { Characters(model::showCharacters, it, toCharacter) }
            }
            manga.personRoles?.let {
                if (it.isNotEmpty()) item { Authors(model::showAuthors, it, toPerson) }
            }
        }
    }

    when {
        state.showSheet -> BottomSheet(
            model::hideSheet, model::showRate, model::showSimilar, model::showStats,
            model::showLinks, model::changeFavourite, state.sheetBottom, manga.userRate, favoured
        )
        state.showComments -> Comments(model::hideComments, comments, toUser)
        state.showRelated -> DialogRelated(model::hideRelated, manga.related!!, toAnime, toManga)
        state.showCharacters -> DialogCharacters(model::hideCharacters, state.lazyCharacters, manga.characterRoles!!, toCharacter)
        state.showAuthors -> DialogAuthors(model::hideAuthors, state.lazyAuthors, manga.personRoles!!, toPerson)
        state.showRate -> CreateRate(model::hideRate, model::reload, LINKED_TYPE[1], manga.id, manga.userRate)
        state.showSimilar -> DialogSimilar(model::hideSimilar, state.lazySimilar, similar, toManga)
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
    toManga: (String) -> Unit
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
                    modifier = Modifier.clickable { toManga(id.toString()) },
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