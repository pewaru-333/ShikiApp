package org.application.shikiapp.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AnimeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.PersonScreenDestination
import org.application.shikiapp.R.drawable.vector_comments
import org.application.shikiapp.R.string.text_anime
import org.application.shikiapp.R.string.text_character
import org.application.shikiapp.R.string.text_seyu
import org.application.shikiapp.R.string.text_show_all_w
import org.application.shikiapp.models.data.AnimeShort
import org.application.shikiapp.models.data.CharacterPerson
import org.application.shikiapp.models.views.CharacterViewModel
import org.application.shikiapp.models.views.CharacterViewModel.Response.Error
import org.application.shikiapp.models.views.CharacterViewModel.Response.Loading
import org.application.shikiapp.models.views.CharacterViewModel.Response.Success
import org.application.shikiapp.models.views.CommentViewModel
import org.application.shikiapp.models.views.factory
import org.application.shikiapp.utils.Preferences
import com.ramcosta.composedestinations.navigation.DestinationsNavigator as Navigator

@Destination<RootGraph>
@Composable
fun CharacterScreen(id: String, navigator: Navigator) {
    val model = viewModel<CharacterViewModel>(factory = factory { CharacterViewModel(id) })
    val response by model.response.collectAsStateWithLifecycle()

    when (val data = response) {
        is Error -> ErrorScreen(model::getCharacter)
        is Loading -> LoadingScreen()
        is Success -> CharacterView(model, data, navigator)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CharacterView(model: CharacterViewModel, data: Success, navigator: Navigator) {
    val character = data.character
    val anime = data.character.animes
    val seyu = data.character.seyu

    val state by model.state.collectAsStateWithLifecycle()
    val comments = character.topicId?.let {
        viewModel<CommentViewModel>(factory = factory { CommentViewModel(it) })
    }?.comments?.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_character)) },
                navigationIcon = { NavigationIcon(navigator::popBackStack)},
                actions = {
                    comments?.let { IconButton(model::showComments) { Icon(painterResource(vector_comments), null) } }
                    if (Preferences.isTokenExists()) IconButton({ model.changeFavourite(data.character.favoured) }) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = if (data.character.favoured) Color.Red else LocalContentColor.current
                        )
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
                Row(horizontalArrangement = spacedBy(8.dp)) {
                    Poster(data.image.poster?.originalUrl)
                    Names(listOf(character.russian, character.japanese, character.altName))
                }
            }

            character.descriptionHTML?.let { if (fromHtml(it).isNotEmpty()) item { Description(it) } }
            anime.let { if (it.isNotEmpty()) item { Anime(model, it, navigator) } }
            seyu.let { if (it.isNotEmpty()) item { Seyu(model, it, navigator) } }
        }
    }

    when {
        state.showAnime -> DialogAnime(model, anime, navigator)
        state.showSeyu -> DialogSeyu(model, seyu, navigator)
        state.showComments -> Comments(model::hideComments, comments!!, navigator)
    }
}

@Composable
private fun Anime(model: CharacterViewModel, list: List<AnimeShort>, navigator: Navigator) =
    Column {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_anime), Modifier.padding(bottom = 4.dp))
            TextButton(model::showAnime) { Text(stringResource(text_show_all_w)) }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), spacedBy(12.dp), CenterVertically) {
            list.take(5).forEach { (id, name, russian, image) ->
                Column(
                    Modifier
                        .width(120.dp)
                        .clickable { navigator.navigate(AnimeScreenDestination(id.toString())) }) {
                    RoundedRelatedPoster(image.original)
                    RelatedText(russian ?: name)
                }
            }
        }
    }

@Composable
private fun Seyu(model: CharacterViewModel, list: List<CharacterPerson>, navigator: Navigator) =
    Column(verticalArrangement = spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_seyu))
            IconButton(model::showSeyu) { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), spacedBy(16.dp), CenterVertically) {
            list.take(3).forEach { (id, name, russian, image) ->
                Column(Modifier.clickable { navigator.navigate(PersonScreenDestination(id)) }) {
                    CircleImage(image.original)
                    TextCircleImage(russian ?: name)
                }
            }
        }
    }

// =========================================== Dialogs ===========================================

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DialogAnime(model: CharacterViewModel, list: List<AnimeShort>, navigator: Navigator) =
    Dialog(model::hideAnime, DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(text_anime)) },
                    navigationIcon = { NavigationIcon(model::hideAnime) }
                )
            }
        ) { values ->
            LazyColumn(
                contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
                verticalArrangement = spacedBy(16.dp)
            ) {
                items(list) { (id, name, russian, image, kind, releasedOn) ->
                    Row(
                        Modifier
                            .height(198.dp)
                            .clickable { navigator.navigate(AnimeScreenDestination(id.toString())) },
                        spacedBy(16.dp)
                    )
                    {
                        RoundedAnimePoster(image.original)
                        AnimeShortDescription(russian ?: name, kind, releasedOn)
                    }
                }
            }
        }
    }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DialogSeyu(
    model: CharacterViewModel,
    list: List<CharacterPerson>,
    navigator: Navigator
) = Dialog(model::hideSeyu, DialogProperties(usePlatformDefaultWidth = false)) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(text_seyu)) },
                navigationIcon = { NavigationIcon(model::hideSeyu) }
            )
        }
    ) { values ->
        LazyColumn(
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding(), 8.dp, 0.dp),
            verticalArrangement = spacedBy(16.dp)
        ) {
            items(list) { (id, name, russian, image) ->
                OneLineImage(
                    name = russian ?: name,
                    link = image.original,
                    modifier = Modifier.clickable { navigator.navigate(PersonScreenDestination(id)) }
                )
            }
        }
    }
}