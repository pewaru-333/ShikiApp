package org.application.shikiapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
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
import com.ramcosta.composedestinations.generated.destinations.CharacterScreenDestination
import org.application.shikiapp.R
import org.application.shikiapp.R.drawable.vector_comments
import org.application.shikiapp.R.string.text_add_fav
import org.application.shikiapp.R.string.text_all_roles
import org.application.shikiapp.R.string.text_best_roles
import org.application.shikiapp.R.string.text_information
import org.application.shikiapp.R.string.text_remove_fav
import org.application.shikiapp.models.data.Person
import org.application.shikiapp.models.data.Roles
import org.application.shikiapp.models.views.CommentViewModel
import org.application.shikiapp.models.views.PersonState
import org.application.shikiapp.models.views.PersonViewModel
import org.application.shikiapp.models.views.PersonViewModel.Response.Error
import org.application.shikiapp.models.views.PersonViewModel.Response.Loading
import org.application.shikiapp.models.views.PersonViewModel.Response.Success
import org.application.shikiapp.models.views.factory
import org.application.shikiapp.utils.Preferences
import org.application.shikiapp.utils.getBirthday
import org.application.shikiapp.utils.getDeathday
import org.application.shikiapp.utils.isPersonFavoured
import com.ramcosta.composedestinations.navigation.DestinationsNavigator as Navigator

@Destination<RootGraph>
@Composable
fun PersonScreen(id: Long, navigator: Navigator) {
    val model = viewModel<PersonViewModel>(factory = factory { PersonViewModel(id) })
    val response by model.response.collectAsStateWithLifecycle()

    when (val data = response) {
        is Error -> ErrorScreen(model::getPerson)
        Loading -> LoadingScreen()
        is Success -> PersonView(model, data.person, navigator)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PersonView(model: PersonViewModel, person: Person, navigator: Navigator) {
    val state by model.state.collectAsStateWithLifecycle()
    val comments = person.topicId?.let {
        viewModel<CommentViewModel>(factory = factory { CommentViewModel(it) })
    }?.comments?.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(person.jobTitle) },
                navigationIcon = { NavigationIcon(navigator::popBackStack) },
                actions = {
                    comments?.let { IconButton(model::showComments) { Icon( painterResource(vector_comments), null) } }
                    if (Preferences.isTokenExists() || person.website.isNotEmpty())
                        IconButton(model::showSheet) { Icon(Icons.Outlined.MoreVert, null) }
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
                    Poster(person.image.original)
                    Column(verticalArrangement = spacedBy(16.dp)) {
                        Names(listOf(person.russian, person.name, person.japanese))
                        getBirthday(person.birthday)?.let { Birthday(it) }
                        getDeathday(person.deceasedOn)?.let { Deathday(it) }
                    }
                }
            }
            person.grouppedRoles?.let { item { Roles(it) } }
            person.roles?.let { if (it.isNotEmpty()) item { Roles(model, it, navigator) } }
        }
    }

    when {
        state.showComments -> Comments(model::hideComments, comments!!, navigator)
        state.showSheet -> BottomSheet(model, state, person)
        state.showRoles -> DialogRoles(model, person.roles!!, navigator)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheet(
    model: PersonViewModel,
    state: PersonState,
    person: Person,
    handler: UriHandler = LocalUriHandler.current
) = ModalBottomSheet(model::hideSheet, sheetState = state.sheetState) {
    if (Preferences.isTokenExists()) ListItem(
        headlineContent = { Text(stringResource(if ((isPersonFavoured(person))) text_remove_fav else text_add_fav)) },
        modifier = Modifier.clickable { model.changeFavourite(person) },
        leadingContent = {
            Icon(Icons.Default.Favorite, null, tint = if (isPersonFavoured(person)) Color.Red else LocalContentColor.current)
        }
    )
    if (person.website.isNotEmpty()) ListItem(
        headlineContent = { Text("Сайт") },
        modifier = Modifier.clickable { handler.openUri(person.website) },
        leadingContent = { Icon(painterResource(R.drawable.vector_website), null) }
    )
    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
}

@Composable
private fun Roles(roles: List<Pair<String, Int>>) = Column(verticalArrangement = spacedBy(8.dp)) {
    ParagraphTitle(stringResource(text_information))
    Column { roles.let { it.forEach { (first, second) -> Text("$first: $second") } } }
}

@Composable
private fun Roles(model: PersonViewModel, roles: List<Roles>?, navigator: Navigator) =
    Column(verticalArrangement = spacedBy(8.dp), horizontalAlignment = Alignment.Start) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_best_roles))
            IconButton(model::showRoles) { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), spacedBy(16.dp)) {
            roles?.take(5)?.forEach { role ->
                role.characters.forEach { (id, name, russian, image) ->
                    Column(
                        modifier = Modifier.clickable {
                            navigator.navigate(CharacterScreenDestination(id.toString()))
                        },
                        verticalArrangement = spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircleImage(image.original)
                        TextCircleImage(russian ?: name)
                    }
                }
            }
        }
    }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DialogRoles(model: PersonViewModel, roles: List<Roles>, navigator: Navigator) =
    Dialog(model::hideRoles, DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(text_all_roles)) },
                    navigationIcon = { NavigationIcon(model::hideRoles) }
                )
            }
        ) { values ->
            LazyColumn(contentPadding = PaddingValues(top = values.calculateTopPadding())) {
                roles.forEach { (characters) ->
                    characters.forEach { (id, name, russian, image) ->
                        item {
                            OneLineImage(
                                name = russian ?: name,
                                link = image.original,
                                modifier = Modifier.clickable {
                                    navigator.navigate(CharacterScreenDestination(id.toString()))
                                }
                            )
                        }
                    }
                }
            }
        }
    }