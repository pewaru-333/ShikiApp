@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.application.shikiapp.shared.screens

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.MangaState
import org.application.shikiapp.shared.models.ui.Manga
import org.application.shikiapp.shared.models.viewModels.MangaViewModel
import org.application.shikiapp.shared.models.viewModels.UserRateViewModel
import org.application.shikiapp.shared.network.response.Response.Success
import org.application.shikiapp.shared.ui.templates.AnimatedScreen
import org.application.shikiapp.shared.ui.templates.BottomSheet
import org.application.shikiapp.shared.ui.templates.Comments
import org.application.shikiapp.shared.ui.templates.DialogEditRate
import org.application.shikiapp.shared.ui.templates.DialogPoster
import org.application.shikiapp.shared.ui.templates.LinkListener
import org.application.shikiapp.shared.ui.templates.LinksSheet
import org.application.shikiapp.shared.ui.templates.ProfilesFull
import org.application.shikiapp.shared.ui.templates.RelatedFull
import org.application.shikiapp.shared.ui.templates.ScaffoldContent
import org.application.shikiapp.shared.ui.templates.SimilarFull
import org.application.shikiapp.shared.ui.templates.Statistics
import org.application.shikiapp.shared.ui.templates.genres
import org.application.shikiapp.shared.ui.templates.info
import org.application.shikiapp.shared.ui.templates.profiles
import org.application.shikiapp.shared.ui.templates.related
import org.application.shikiapp.shared.ui.templates.summary
import org.application.shikiapp.shared.ui.templates.title
import org.application.shikiapp.shared.utils.enums.LinkedType
import org.application.shikiapp.shared.utils.navigation.Screen
import org.application.shikiapp.shared.utils.viewModel
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_authors
import shikiapp.composeapp.generated.resources.text_characters

@Composable
fun MangaScreen(onNavigate: (Screen) -> Unit, back: () -> Unit) {
    val model = viewModel(::MangaViewModel)
    val response by model.response.collectAsStateWithLifecycle()
    val state by model.state.collectAsStateWithLifecycle()

    LinkListener(model.openLink) { (response as? Success)?.data?.url }

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
    val rateModel = viewModel(::UserRateViewModel)
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
            title = Res.string.text_characters,
            onShow = { onEvent(ContentDetailEvent.Media.ShowCharacters) },
            onNavigate = { onNavigate(Screen.Character(it)) }
        )
        profiles(
            profiles = manga.personMain,
            title = Res.string.text_authors,
            onShow = { onEvent(ContentDetailEvent.Media.ShowAuthors) },
            onNavigate = { onNavigate(Screen.Person(it.toLong())) }
        )
    }

    Comments(
        list = comments,
        listState = commentsState,
        isVisible = state.showComments,
        onHide = { onEvent(ContentDetailEvent.ShowComments) },
        onNavigate = onNavigate
    )

    RelatedFull(
        related = manga.related,
        chronology = manga.chronology,
        franchise = manga.franchiseList,
        isVisible = state.showRelated,
        onHide = { onEvent(ContentDetailEvent.Media.ShowRelated) },
        onNavigate = onNavigate
    )

    ProfilesFull(
        list = if (state.showCharacters) manga.charactersAll else manga.personAll,
        isVisible = state.showCharacters || state.showAuthors,
        title = stringResource(if (state.showCharacters) Res.string.text_characters else Res.string.text_authors),
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
        onHide = { onEvent(ContentDetailEvent.Media.ShowSimilar) },
        listState = similarState,
        isVisible = state.showSimilar,
        list = manga.similar,
        onNavigate = { onNavigate(Screen.Manga(it)) }
    )

    Statistics(
        statistics = manga.stats,
        isVisible = state.showStats,
        onHide = { onEvent(ContentDetailEvent.Media.ShowStats) }
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