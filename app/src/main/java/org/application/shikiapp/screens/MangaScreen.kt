@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.screens

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.collectLatest
import org.application.shikiapp.R
import org.application.shikiapp.events.ContentDetailEvent
import org.application.shikiapp.models.states.MangaState
import org.application.shikiapp.models.ui.Manga
import org.application.shikiapp.models.viewModels.MangaViewModel
import org.application.shikiapp.models.viewModels.UserRateViewModel
import org.application.shikiapp.network.response.Response.Success
import org.application.shikiapp.ui.templates.AnimatedScreen
import org.application.shikiapp.ui.templates.BottomSheet
import org.application.shikiapp.ui.templates.Comments
import org.application.shikiapp.ui.templates.DialogEditRate
import org.application.shikiapp.ui.templates.DialogPoster
import org.application.shikiapp.ui.templates.LinksSheet
import org.application.shikiapp.ui.templates.ProfilesFull
import org.application.shikiapp.ui.templates.RelatedFull
import org.application.shikiapp.ui.templates.ScaffoldContent
import org.application.shikiapp.ui.templates.SimilarFull
import org.application.shikiapp.ui.templates.Statistics
import org.application.shikiapp.ui.templates.genres
import org.application.shikiapp.ui.templates.info
import org.application.shikiapp.ui.templates.profiles
import org.application.shikiapp.ui.templates.related
import org.application.shikiapp.ui.templates.summary
import org.application.shikiapp.ui.templates.title
import org.application.shikiapp.utils.enums.LinkedType
import org.application.shikiapp.utils.extensions.openLinkInBrowser
import org.application.shikiapp.utils.navigation.Screen

@Composable
fun MangaScreen(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val context = LocalContext.current

    val model = viewModel<MangaViewModel>()
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    LaunchedEffect(model.openLink) {
        model.openLink.collectLatest {
            context.openLinkInBrowser((response as Success).data.url)
        }
    }

    AnimatedScreen(response, model::loadData) { manga ->
        MangaView(manga, state, model::onEvent, onNavigate, back)
    }
}

@Composable
private fun MangaView(
    manga: Manga,
    state: MangaState,
    onEvent: (ContentDetailEvent) -> Unit,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    val rateModel = viewModel<UserRateViewModel>()
    val rate = manga.userRate.getValue()
    val newRate by rateModel.newRate.collectAsStateWithLifecycle()

    val commentsState = rememberLazyListState()
    val authorsState = rememberLazyListState()
    val charactersState = rememberLazyListState()
    val similarState = rememberLazyListState()

    val comments = manga.comments.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        rate?.let { rateModel.getRate(it, LinkedType.MANGA) }
    }

    val onShowLinks = remember {
        if (manga.links.isEmpty()) null
        else {
            { onEvent(ContentDetailEvent.Media.ShowLinks) }
        }
    }

    ScaffoldContent(
        title = { Text(stringResource(manga.kindTitle)) },
        userRate = manga.userRate,
        isFavoured = manga.favoured,
        onBack = onBack,
        onEvent = onEvent,
        onToggleFavourite = { onEvent(ContentDetailEvent.Media.Manga.ToggleFavourite(manga.kindEnum)) },
        onLoadState = { (comments.loadState.refresh is LoadState.Loading) to comments.itemCount }
    ) {
        title(manga.title)
        info(
            poster = manga.poster,
            kind = manga.kindString,
            score = manga.score,
            status = manga.status,
            airedOn = manga.airedOn,
            releasedOn = manga.releasedOn,
            volumes = manga.volumes,
            chapters = manga.chapters,
            isOngoingManga = manga.isOngoing,
            publisher = manga.publisher,
            onOpenFullscreenPoster = { onEvent(ContentDetailEvent.Media.ShowPoster) }
        )

        genres(manga.genres)
        summary(
            similar = manga.similar,
            publisher = manga.publisher,
            linkedType = manga.kindEnum.linkedType,
            onEvent = onEvent,
            onNavigate = onNavigate
        )

        related(
            related = manga.related,
            onShow = { onEvent(ContentDetailEvent.Media.ShowRelated) },
            onNavigate = onNavigate
        )
        profiles(
            profiles = manga.charactersMain,
            title = R.string.text_characters,
            onShow = { onEvent(ContentDetailEvent.Media.ShowCharacters) },
            onNavigate = { onNavigate(Screen.Character(it)) }
        )
        profiles(
            profiles = manga.personMain,
            title = R.string.text_authors,
            onShow = { onEvent(ContentDetailEvent.Media.ShowAuthors) },
            onNavigate = { onNavigate(Screen.Person(it.toLong())) }
        )
    }

    Comments(
        list = comments,
        listState = commentsState,
        visible = state.showComments,
        hide = { onEvent(ContentDetailEvent.ShowComments) },
        onNavigate = onNavigate
    )

    RelatedFull(
        related = manga.related,
        chronology = manga.chronology,
        franchise = manga.franchiseList,
        visible = state.showRelated,
        hide = { onEvent(ContentDetailEvent.Media.ShowRelated) },
        onNavigate = onNavigate
    )

    ProfilesFull(
        list = if (state.showCharacters) manga.charactersAll else manga.personAll,
        visible = state.showCharacters || state.showAuthors,
        title = stringResource(if (state.showCharacters) R.string.text_characters else R.string.text_authors),
        state = if (state.showCharacters) charactersState else authorsState,
        onHide = {
            onEvent(
                if (state.showCharacters) ContentDetailEvent.Media.ShowCharacters
                else ContentDetailEvent.Media.ShowAuthors
            )
        },
        onNavigate = {
            onNavigate(
                if (state.showCharacters) Screen.Character(it)
                else Screen.Person(it.toLong())
            )
        }
    )

    SimilarFull(
        hide = { onEvent(ContentDetailEvent.Media.ShowSimilar) },
        listState = similarState,
        visible = state.showSimilar,
        list = manga.similar,
        onNavigate = { onNavigate(Screen.Manga(it)) }
    )

    Statistics(
        statistics = manga.stats,
        visible = state.showStats,
        hide = { onEvent(ContentDetailEvent.Media.ShowStats) }
    )

    DialogPoster(
        link = manga.poster,
        isVisible = state.showPoster,
        onClose = { onEvent(ContentDetailEvent.Media.ShowPoster) }
    )

    when {
        state.showRate -> DialogEditRate(
            state = newRate,
            type = LinkedType.MANGA,
            isExists = rate != null,
            onEvent = rateModel::onEvent,
            onDismiss = { onEvent(ContentDetailEvent.Media.ShowRate) },
            onCreate = { type ->
                rateModel.create(
                    id = manga.id,
                    targetType = type,
                    reload = { onEvent(ContentDetailEvent.Media.ChangeRate) }
                )
            },
            onUpdate = {
                rateModel.update(
                    rateId = rate?.id.toString(),
                    reload = { onEvent(ContentDetailEvent.Media.ChangeRate) }
                )
            },
            onDelete = {
                rateModel.delete(
                    rateId = rate?.id.toString(),
                    reload = { onEvent(ContentDetailEvent.Media.ChangeRate) }
                )
            }
        )

        state.showSheet -> BottomSheet(
            url = manga.url,
            canShowLinks = manga.links.isNotEmpty(),
            onEvent = onEvent
        )

        state.showLinks -> LinksSheet(manga.links) {
            onEvent(ContentDetailEvent.Media.ShowLinks)
        }
    }
}