package org.application.shikiapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.application.shikiapp.models.views.CalendarViewModel
import org.application.shikiapp.models.views.CalendarViewModel.Response.Error
import org.application.shikiapp.models.views.CalendarViewModel.Response.Loading
import org.application.shikiapp.models.views.CalendarViewModel.Response.Success
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.toCalendarDate

@Composable
fun CalendarScreen(toAnime:(String) -> Unit) {
    val model = viewModel<CalendarViewModel>()
    val state by model.state.collectAsStateWithLifecycle()

    when (val data = state) {
        Error -> ErrorScreen(model::getCalendar)
        Loading -> LoadingScreen()
        is Success -> LazyColumn(
            contentPadding = PaddingValues(8.dp, 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(data.calendar) { (date, list) ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ParagraphTitle(toCalendarDate(date))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(list) { (_, _, _, anime) ->
                            Column (
                                modifier = Modifier
                                    .width(122.dp)
                                    .clickable { toAnime(anime.id.toString()) },
                            ) {
                                RoundedRelatedPoster(anime.image.original, ContentScale.FillBounds)
                                Text(
                                    text = anime.russian?.ifEmpty { anime.name } ?: BLANK,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    maxLines = 3,
                                    minLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}