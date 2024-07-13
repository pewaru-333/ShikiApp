package org.application.shikiapp.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import org.application.shikiapp.models.views.CalendarResponse
import org.application.shikiapp.models.views.CalendarViewModel
import org.application.shikiapp.utils.fromISODate
import org.application.shikiapp.utils.getImage
import org.application.shikiapp.utils.toCalendarDate

@Destination<RootGraph>
@Composable
fun CalendarScreen(navigator: DestinationsNavigator) {
    val viewModel: CalendarViewModel = viewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val data = state) {
        is CalendarResponse.Error -> ErrorScreen(viewModel.getCalendar())
        CalendarResponse.Loading -> LoadingScreen()
        is CalendarResponse.Success -> {
            val list = data.calendar.groupBy { fromISODate(it.nextEpisodeAt) }

            LazyColumn(
                contentPadding = PaddingValues(8.dp, 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                list.entries.forEach { entry ->
                    item {
                        Column(Modifier.fillMaxWidth(), Arrangement.spacedBy(4.dp)) {
                            ParagraphTitle(toCalendarDate(entry.key))
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                entry.value.forEach { (_, _, _, anime) ->
                                    Column(Modifier.clickable {
                                        navigator.navigate(
                                            AnimeScreenDestination(anime.id.toString())
                                        )
                                    }) {
                                        AsyncImage(
                                            model = getImage(anime.image.original),
                                            modifier = Modifier
                                                .size(160.dp, 225.dp)
                                                .clip(MaterialTheme.shapes.medium)
                                                .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium),
                                            contentDescription = null,
                                            contentScale = ContentScale.FillBounds,
                                            filterQuality = FilterQuality.High
                                        )
                                        Text(
                                            text = anime.russian.ifEmpty { anime.name },
                                            modifier = Modifier.width(160.dp),
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
            }
        }
    }
}


