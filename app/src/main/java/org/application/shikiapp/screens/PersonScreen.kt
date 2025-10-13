@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import org.application.shikiapp.R
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.PersonState
import org.application.shikiapp.models.ui.Person
import org.application.shikiapp.models.viewModels.PersonViewModel
import org.application.shikiapp.network.response.Response.Success
import org.application.shikiapp.ui.templates.AnimatedScreen
import org.application.shikiapp.ui.templates.BottomSheet
import org.application.shikiapp.ui.templates.Comments
import org.application.shikiapp.ui.templates.IconComment
import org.application.shikiapp.ui.templates.Names
import org.application.shikiapp.ui.templates.NavigationIcon
import org.application.shikiapp.ui.templates.ParagraphTitle
import org.application.shikiapp.ui.templates.Profiles
import org.application.shikiapp.ui.templates.ProfilesFull
import org.application.shikiapp.ui.templates.Related
import org.application.shikiapp.ui.templates.RelatedFull
import org.application.shikiapp.ui.templates.VectorIcon
import org.application.shikiapp.utils.extensions.openLinkInBrowser
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun PersonScreen(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val context = LocalContext.current

    val model = viewModel<PersonViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    LaunchedEffect(model.openLink) {
        model.openLink.collectLatest {
            context.openLinkInBrowser((response as Success).data.url)
        }
    }

    AnimatedScreen(response, model::loadData) { person ->
        PersonView(person, state, model::onEvent, onNavigate, back)
    }
}

@Composable
private fun PersonView(
    person: Person,
    state: PersonState,
    onEvent: (ContentDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    back: () -> Unit
) {
    val listState = rememberLazyListState()
    val comments = person.comments.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(person.jobTitle) },
                navigationIcon = { NavigationIcon(back) },
                actions = {
                    IconComment(
                        comments = comments,
                        onEvent = { onEvent(ContentDetailEvent.ShowComments) }
                    )
                    IconButton(
                        onClick = { onEvent(ContentDetailEvent.ShowSheet) },
                        content = { VectorIcon(R.drawable.vector_more) }
                    )
                }
            )
        }
    ) { values ->
        LazyColumn(
            contentPadding = PaddingValues(8.dp, values.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                PersonHeader(person)
            }

            item {
                Roles(person.grouppedRoles)
            }

            person.relatedList.let {
                if (it.isNotEmpty()) {
                    item {
                        Related(
                            list = it.take(6),
                            showAllRelated = { onEvent(ContentDetailEvent.Person.ShowWorks) },
                            onNavigate = onNavigate
                        )
                    }
                }
            }

            person.characters.let {
                if (it.isNotEmpty()) {
                    item {
                        Profiles(
                            list = it.take(6),
                            title = stringResource(R.string.text_characters),
                            onShowFull = { onEvent(ContentDetailEvent.Media.ShowCharacters) },
                            onNavigate = { onNavigate(Screen.Character(it)) }
                        )
                    }
                }
            }
        }
    }

    Comments(
        list = comments,
        listState = listState,
        visible = state.showComments,
        hide = { onEvent(ContentDetailEvent.ShowComments) },
        onNavigate = onNavigate
    )

    ProfilesFull(
        list = person.characters,
        visible = state.showCharacters,
        title = stringResource(R.string.text_characters),
        state = rememberLazyListState(),
        onHide = { onEvent(ContentDetailEvent.Media.ShowCharacters) },
        onNavigate = { onNavigate(Screen.Character(it)) }
    )

    RelatedFull(
        related = person.relatedMap,
        visible = state.showWorks,
        hide = { onEvent(ContentDetailEvent.Person.ShowWorks) },
        onNavigate = onNavigate
    )

    if (state.showSheet) {
        BottomSheet(
            website = person.website,
            kind = person.personKind,
            favoured = person.favoured,
            onEvent = onEvent
        )
    }
}

@Composable
private fun PersonHeader(person: Person) =
    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp), Alignment.CenterVertically) {
        AsyncImage(
            model = person.image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(130.dp)
                .aspectRatio(2f / 3f)
                .clip(MaterialTheme.shapes.medium)
                .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium),
        )

        Column(Modifier.weight(1f), Arrangement.spacedBy(12.dp)) {
            Names(person.russian, person.english, person.japanese)

            if (person.birthday != null || person.deathday != null) {
                HorizontalDivider()
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                person.birthday?.let {
                    LabelInfoItem(stringResource(R.string.text_birthday), it)
                }
                person.deathday?.let {
                    LabelInfoItem(stringResource(R.string.text_deathday), it)
                }
            }
        }
    }

@Composable
fun LabelInfoItem(label: String, value: String, modifier: Modifier = Modifier) =
    Column(modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Light
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }

@Composable
private fun Roles(roles: List<List<String>>) =
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ParagraphTitle(stringResource(R.string.text_roles))
        Column {
            roles.forEachIndexed { index, (name, count) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                ) {
                    Text(
                        text = name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = count,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                if (index < roles.size - 1) {
                    HorizontalDivider()
                }
            }
        }
    }