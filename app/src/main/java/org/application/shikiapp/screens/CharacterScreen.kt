package org.application.shikiapp.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.ramcosta.composedestinations.generated.destinations.AnimeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.PersonScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.application.shikiapp.R
import org.application.shikiapp.R.string.text_anime
import org.application.shikiapp.R.string.text_character
import org.application.shikiapp.R.string.text_seyu
import org.application.shikiapp.R.string.text_show_all_m
import org.application.shikiapp.R.string.text_show_all_w
import org.application.shikiapp.models.data.AnimeWork
import org.application.shikiapp.models.data.Person
import org.application.shikiapp.models.views.CharacterResponse
import org.application.shikiapp.models.views.CharacterViewModel
import org.application.shikiapp.models.views.CommentViewModel
import org.application.shikiapp.models.views.factory
import org.application.shikiapp.utils.getImage
import org.application.shikiapp.utils.getKind
import org.application.shikiapp.utils.getSeason

@Destination<RootGraph>
@Composable
fun CharacterScreen(id: String, navigator: DestinationsNavigator) {
    val model = viewModel<CharacterViewModel>(factory = factory { CharacterViewModel(id) })
    val response by model.response.collectAsStateWithLifecycle()

    when (val data = response) {
        is CharacterResponse.Error -> ErrorScreen()
        is CharacterResponse.Loading -> LoadingScreen()
        is CharacterResponse.Success -> CharacterView(data, navigator)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CharacterView(data: CharacterResponse.Success, navigator: DestinationsNavigator) {
    val character = data.character.character
    val anime = data.character.anime
    val seyu = data.character.seyu

    val comments = character.topic?.id?.let {
        viewModel<CommentViewModel>(factory = factory { CommentViewModel(it.toLong()) })
    }?.comments?.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_character)) },
                navigationIcon = { NavigationIcon(navigator::popBackStack)}
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(8.dp, paddingValues.calculateTopPadding()),
            verticalArrangement = spacedBy(16.dp)
        ) {
            item {
                Row(horizontalArrangement = spacedBy(8.dp)) {
                    Poster(character.poster?.originalUrl)
                    Names(
                        listOf(
                            character.russian,
                            character.japanese,
                            character.synonyms.joinToString(", ")
                        )
                    )
                }
            }
            item { Description(character.descriptionHtml) }
            item { Anime(anime, navigator) }
            item { Seyu(seyu, navigator) }
            comments?.let { comments(it, navigator) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Anime(animeList: List<AnimeWork>, navigator: DestinationsNavigator) {
    var show by remember { mutableStateOf(false) }

    Column {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_anime))
            TextButton({ show = true }) { Text(stringResource(text_show_all_w)) }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), spacedBy(12.dp), CenterVertically) {
            animeList.take(5).forEach { (id, name, russian, image) ->
                Column(
                    Modifier
                        .width(100.dp)
                        .clickable { navigator.navigate(AnimeScreenDestination(id.toString())) }) {
                    AsyncImage(
                        model = getImage(image.original),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .border(1.dp, Color.LightGray),
                        error = painterResource(R.drawable.vector_home),
                        fallback = painterResource(R.drawable.vector_home),
                        contentScale = ContentScale.FillHeight,
                        filterQuality = FilterQuality.High,
                    )
                    Text(
                        text = russian ?: name,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 3,
                        minLines = 3,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

    if (show) Dialog({ show = false }, DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(text_anime)) },
                    navigationIcon = { NavigationIcon { show = false } }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                contentPadding = PaddingValues(8.dp, paddingValues.calculateTopPadding()),
                verticalArrangement = spacedBy(16.dp)
            ) {
                items(animeList) {
                    Row(
                        modifier = Modifier.clickable { navigator.navigate(AnimeScreenDestination(it.id.toString())) },
                        horizontalArrangement = spacedBy(16.dp)
                    )
                    {
                        AsyncImage(
                            model = getImage(it.image.original),
                            modifier = Modifier
                                .size(160.dp, 225.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            filterQuality = FilterQuality.High
                        )
                        Column(verticalArrangement =  spacedBy(4.dp)) {
                            Text(
                                text = it.russian ?: it.name,
                                maxLines = 5,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(text = getKind(it.kind), style = MaterialTheme.typography.bodyLarge)
                            Text(text = getSeason(it.releasedOn), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Seyu(seyuList: List<Person>, navigator: DestinationsNavigator) {
    var show by remember { mutableStateOf(false) }

    Column(verticalArrangement = spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_seyu))
            TextButton({ show = true }) { Text(stringResource(text_show_all_m)) }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), spacedBy(16.dp), CenterVertically) {
            seyuList.take(3).forEach { (id, name, russian, image) ->
                Column(Modifier.clickable { navigator.navigate(PersonScreenDestination(id)) }) {
                    CircleImage(image.original, 76.dp)
                    NameCircleImage(russian.ifEmpty { name }, 76.dp)
                }
            }
        }
    }

    if (show) Dialog({ show = false }, DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(text_seyu)) },
                    navigationIcon = { NavigationIcon { show = false } }
                )
            }
        ) { paddingValues ->
            Column(Modifier.padding(top = paddingValues.calculateTopPadding())) {
                seyuList.forEach { (id, name, russian, image) ->
                    SmallItem(
                        name = russian.ifEmpty { name },
                        link = image.original,
                        modifier = Modifier.clickable { navigator.navigate(PersonScreenDestination(id)) }
                    )
                }
            }
        }
    }
}