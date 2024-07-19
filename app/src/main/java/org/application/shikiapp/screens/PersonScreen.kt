package org.application.shikiapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.CharacterScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.application.shikiapp.R.string.text_all_roles
import org.application.shikiapp.R.string.text_best_roles
import org.application.shikiapp.R.string.text_information
import org.application.shikiapp.R.string.text_show_all_w
import org.application.shikiapp.models.data.Person
import org.application.shikiapp.models.data.Roles
import org.application.shikiapp.models.views.CommentViewModel
import org.application.shikiapp.models.views.PersonResponse
import org.application.shikiapp.models.views.PersonViewModel
import org.application.shikiapp.models.views.factory
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.DATE_FORMATS
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle

@Destination<RootGraph>
@Composable
fun PersonScreen(id: Long, navigator: DestinationsNavigator) {
    val viewModel = viewModel<PersonViewModel>(factory = factory { PersonViewModel(id) })
    val response by viewModel.response.collectAsStateWithLifecycle()

    when (val data = response) {
        is PersonResponse.Error -> ErrorScreen()
        is PersonResponse.Loading -> LoadingScreen()
        is PersonResponse.Success -> PersonView(data.person, navigator)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PersonView(person: Person, navigator: DestinationsNavigator) {
    val comments = person.topicId?.let {
        viewModel<CommentViewModel>(factory = factory { CommentViewModel(it) })
    }?.comments?.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(person.jobTitle ?: "Личность") },
                navigationIcon = { NavigationIcon(navigator::popBackStack) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(8.dp, paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Poster(person.image.original)
                    Names(listOf(person.russian, person.name, person.japanese))
                }
            }
            item { Description(person) }
            item { Roles(person.roles, navigator) }
            comments?.let { comments(it, navigator) }
        }
    }
}

@Composable
private fun Description(person: Person) {
    val birthday = DATE_FORMATS.firstNotNullOfOrNull {
        try {
            LocalDate.parse(
                "${person.birthday.day}.${person.birthday.month}.${person.birthday.year}"
                    .replace("null", BLANK), DateTimeFormatter.ofPattern(it)
            ).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
        } catch (e: DateTimeParseException) {
            null
        }
    }

    val deathday = DATE_FORMATS.firstNotNullOfOrNull {
        try {
            LocalDate.parse(
                "${person.deceasedOn.day}.${person.deceasedOn.month}.${person.deceasedOn.year}"
                    .replace("null", BLANK), DateTimeFormatter.ofPattern(it)
            ).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
        } catch (e: DateTimeParseException) {
            null
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ParagraphTitle(stringResource(text_information))
        Column {
            birthday?.let { Text(text = "Дата рождения: $it", fontSize = 18.sp) }
            deathday?.let { Text(text = "Дата смерти: $it", fontSize = 18.sp) }
            person.grouppedRoles?.let { roles ->
                roles.forEach { Text(text = "${it.first}: ${it.second}", fontSize = 18.sp) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Roles(roles: List<Roles>?, navigator: DestinationsNavigator) {
    var show by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.Start) {
        Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
            ParagraphTitle(stringResource(text_best_roles))
            TextButton({ show = true }) { Text(stringResource(text_show_all_w)) }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), Arrangement.spacedBy(16.dp)) {
            roles?.take(5)?.forEach { role ->
                role.characters.forEach { (id, name, russian, image) ->
                    Column(
                        modifier = Modifier.clickable {
                            navigator.navigate(CharacterScreenDestination(id.toString()))
                        },
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircleImage(image.original)
                        TextCircleImage(russian ?: name)
                    }
                }
            }
        }

        if (show) Dialog({ show = false }, DialogProperties(usePlatformDefaultWidth = false)) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(text_all_roles)) },
                        navigationIcon = { NavigationIcon { show = false } }
                    )
                }
            ) { paddingValues ->
                Column(Modifier.padding(top = paddingValues.calculateTopPadding())) {
                    roles?.forEach { role ->
                        role.characters.forEach { (id, name, russian, image) ->
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
}