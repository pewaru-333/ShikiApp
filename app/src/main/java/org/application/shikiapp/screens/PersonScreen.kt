@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
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
import org.application.shikiapp.ui.templates.DialogPoster
import org.application.shikiapp.ui.templates.Names
import org.application.shikiapp.ui.templates.ParagraphTitle
import org.application.shikiapp.ui.templates.Poster
import org.application.shikiapp.ui.templates.ProfilesFull
import org.application.shikiapp.ui.templates.RelatedFull
import org.application.shikiapp.ui.templates.ScaffoldContent
import org.application.shikiapp.ui.templates.profiles
import org.application.shikiapp.ui.templates.related
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
    onBack: () -> Unit
) {
    val commentsState = rememberLazyListState()
    val comments = person.comments.collectAsLazyPagingItems()

    ScaffoldContent(
        title = { Text(person.jobTitle) },
        userRate = null,
        isFavoured = person.favoured,
        onBack = onBack,
        onLoadState = { (comments.loadState.refresh is LoadState.Loading) to comments.itemCount },
        onEvent = onEvent,
        onToggleFavourite = { onEvent(ContentDetailEvent.Person.ToggleFavourite(person.personKind)) }
    ) {
        item {
            PersonHeader(
                person = person,
                onOpenPoster = { onEvent(ContentDetailEvent.Media.ShowPoster) }
            )
        }

        item {
            Roles(person.grouppedRoles)
        }

        related(
            related = person.relatedList,
            onNavigate = onNavigate,
            onShow = { onEvent(ContentDetailEvent.Person.ShowWorks) }
        )

        profiles(
            profiles = person.characters,
            title = R.string.text_characters,
            onShow = { onEvent(ContentDetailEvent.Media.ShowCharacters) },
            onNavigate = { onNavigate(Screen.Character(it)) }
        )
    }

    Comments(
        list = comments,
        listState = commentsState,
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

    DialogPoster(
        link = person.image,
        isVisible = state.showPoster,
        onClose = { onEvent(ContentDetailEvent.Media.ShowPoster) }
    )

    if (state.showSheet) {
        BottomSheet(
            url = person.url,
            website = person.website,
            onEvent = onEvent
        )
    }
}

@Composable
private fun PersonHeader(person: Person, onOpenPoster: () -> Unit) =
    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp), Alignment.CenterVertically) {
        Poster(
            link = person.image,
            contentScale = ContentScale.Crop,
            onOpenFullscreen = onOpenPoster,
            modifier = Modifier
                .width(130.dp)
                .aspectRatio(2f / 3f)
        )

        Column(Modifier.weight(1f), Arrangement.spacedBy(12.dp)) {
            Names(person.russian, person.english, person.japanese)

            if (person.birthday != null || person.deathday != null) {
                HorizontalDivider()
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                person.birthday?.let {
                    LabelInfoItem(stringResource(R.string.text_birthday), it.asString())
                }
                person.deathday?.let {
                    LabelInfoItem(stringResource(R.string.text_deathday), it.asString())
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