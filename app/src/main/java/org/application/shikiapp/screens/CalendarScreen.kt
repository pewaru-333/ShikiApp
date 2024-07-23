package org.application.shikiapp.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AnimeScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.application.shikiapp.models.views.AnimeSchedule
import org.application.shikiapp.models.views.CalendarViewModel
import org.application.shikiapp.models.views.CalendarViewModel.Response.Error
import org.application.shikiapp.models.views.CalendarViewModel.Response.Loading
import org.application.shikiapp.models.views.CalendarViewModel.Response.Success
import org.application.shikiapp.utils.getImage
import org.application.shikiapp.utils.toCalendarDate

@Destination<RootGraph>
@Composable
fun CalendarScreen(navigator: DestinationsNavigator) {
    val model: CalendarViewModel = viewModel()
    val state by model.state.collectAsStateWithLifecycle()

    when (val data = state) {
        Error -> ErrorScreen(model::getCalendar)
        Loading -> LoadingScreen()
        is Success -> CalendarView(data.calendar, navigator)
    }
}

@Composable
private fun CalendarView(data: List<AnimeSchedule>, navigator: DestinationsNavigator) = LazyColumn(
    contentPadding = PaddingValues(8.dp, 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    items(data, { it.date }) { (date, list) ->
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ParagraphTitle(toCalendarDate(date))
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()), Arrangement.spacedBy(16.dp)
            ) {
                list.forEach { (_, _, _, anime) ->
                    Column(
                        Modifier
                            .width(140.dp)
                            .clickable {
                                navigator.navigate(AnimeScreenDestination(anime.id.toString()))
                            }) {
                        AsyncImage(
                            model = getImage(anime.image.original),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(197.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .border(
                                    (0.5).dp,
                                    MaterialTheme.colorScheme.onSurface,
                                    MaterialTheme.shapes.medium
                                ),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            filterQuality = FilterQuality.High
                        )
                        Text(
                            text = anime.russian.ifEmpty { anime.name },
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
        }
    }
}


