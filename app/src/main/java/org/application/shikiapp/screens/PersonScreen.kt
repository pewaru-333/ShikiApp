package org.application.shikiapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import org.application.shikiapp.R
import org.application.shikiapp.R.drawable.vector_comments
import org.application.shikiapp.R.string.text_add_fav
import org.application.shikiapp.R.string.text_information
import org.application.shikiapp.R.string.text_remove_fav
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.PersonState
import org.application.shikiapp.models.ui.Person
import org.application.shikiapp.models.viewModels.PersonViewModel
import org.application.shikiapp.network.response.Response
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun PersonScreen(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val model = viewModel<PersonViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    when (val data = response) {
        is Response.Error -> ErrorScreen(model::loadData)
        is Response.Loading -> LoadingScreen()
        is Response.Success -> PersonView(data.data, state, model::onEvent, onNavigate, back)
        else -> Unit
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PersonView(
    person: Person,
    state: PersonState,
    onEvent: (ContentDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    back: () -> Unit
) {
    val comments = person.comments.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(person.jobTitle) },
                navigationIcon = { NavigationIcon(back) },
                actions = {
                    if (comments.itemCount > 0)
                        IconButton(
                            onClick = { onEvent(ContentDetailEvent.ShowComments) }
                        ) {
                            Icon(painterResource(vector_comments), null)
                        }
                    if (Preferences.token != null || person.website.isNotEmpty())
                        IconButton(
                            onClick = { onEvent(ContentDetailEvent.ShowSheet) }
                        ) {
                            Icon(Icons.Outlined.MoreVert, null)
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
                    Poster(person.image)
                    Column(verticalArrangement = spacedBy(16.dp)) {
                        Names(listOf(person.russian, person.english, person.japanese))
                        person.birthday?.let { Birthday(it) }
                        person.deathday?.let { Deathday(it) }
                    }
                }
            }
            person.grouppedRoles.let {
                item { Roles(it) }
            }
            person.characters.let {
                if (it.isNotEmpty()) item {
                    Characters(
                        list = it.take(7),
                        show = { onEvent(ContentDetailEvent.Media.ShowCharacters) },
                        onNavigate = onNavigate
                    )
                }
            }
        }
    }

    Comments(
        list = comments,
        visible = state.showComments,
        hide = { onEvent(ContentDetailEvent.ShowComments) },
        onNavigate = onNavigate
    )

    CharactersFull(
        list = person.characters,
        state = rememberLazyListState(),
        visible = state.showCharacters,
        hide = { onEvent(ContentDetailEvent.Media.ShowCharacters) },
        onNavigate = onNavigate
    )

    if (state.showSheet) BottomSheet(person, state, onEvent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheet(
    person: Person,
    state: PersonState,
    onEvent: (ContentDetailEvent) -> Unit,
    handler: UriHandler = LocalUriHandler.current
) = ModalBottomSheet(
    sheetState = state.sheetState,
    onDismissRequest = { onEvent(ContentDetailEvent.ShowSheet) },
    contentWindowInsets = { WindowInsets.systemBars }
) {
    if (Preferences.token != null) ListItem(
        headlineContent = {
            Text(
                text = stringResource(
                    if (person.isPersonFavoured) text_remove_fav else text_add_fav
                )
            )
        },
        modifier = Modifier.clickable {
            onEvent(ContentDetailEvent.Person.ToggleFavourite(person.personKind, person.isPersonFavoured))
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = if (person.isPersonFavoured) Color.Red else LocalContentColor.current
            )
        }
    )
    if (person.website.isNotEmpty()) ListItem(
        headlineContent = { Text("Сайт") },
        modifier = Modifier.clickable { handler.openUri(person.website) },
        leadingContent = { Icon(painterResource(R.drawable.vector_website), null) }
    )
}

@Composable
private fun Roles(roles: List<List<String>>) = Column(verticalArrangement = spacedBy(8.dp)) {
    ParagraphTitle(stringResource(text_information))
    Column { roles.let { it.forEach { (first, second) -> Text("$first: $second") } } }
}