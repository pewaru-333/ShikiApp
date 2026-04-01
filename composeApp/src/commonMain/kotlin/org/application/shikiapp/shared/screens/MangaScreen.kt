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
import org.application.shikiapp.shared.events.ContentDetailEvent
import org.application.shikiapp.shared.models.states.BaseDialogState
import org.application.shikiapp.shared.models.states.MangaState
import org.application.shikiapp.shared.models.states.showAuthors
import org.application.shikiapp.shared.models.states.showCharacters
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
import org.application.shikiapp.shared.utils.ui.rememberCommentListState
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

    AnimatedScreen(response, model::loadData, Manga::comments) { manga, comments ->
        MangaView(manga, state, model::onEvent, onNavigate, back)

        val commentListState = rememberCommentListState(
            list = comments,
            onCommentEvent = model.commentEvent
        )
        Comments(
            state = commentListState,
            isVisible = state.dialogState is BaseDialogState.Comments,
            isSending = state.isSendingComment,
            onNavigate = onNavigate,
            onHide = { model.onEvent(ContentDetailEvent.ToggleDialog(null)) },
            onSendComment = { text, isOfftopic ->
                model.onEvent(ContentDetailEvent.SendComment(text, isOfftopic))
            }
        )
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

    val authorsState = rememberLazyListState()
    val charactersState = rememberLazyListState()
    val similarState = rememberLazyListState()

    LaunchedEffect(Unit) {
        rate?.let { rateModel.getRate(it, LinkedType.MANGA) }
    }

    ScaffoldContent(
        title = { Text(stringResource(manga.kindTitle)) },
        userRate = manga.userRate,
        isFavoured = manga.favoured,
        onBack = onBack,
        onEvent = onEvent,
        onToggleFavourite = { onEvent(ContentDetailEvent.Media.Manga.ToggleFavourite(manga.kindEnum)) }
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
            onOpenFullscreenPoster = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Poster)) }
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
            list = manga.related,
            onShow = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Related)) },
            onNavigate = onNavigate
        )
        profiles(
            profiles = manga.charactersMain,
            title = Res.string.text_characters,
            onShow = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Characters)) },
            onNavigate = { onNavigate(Screen.Character(it)) }
        )
        profiles(
            profiles = manga.personMain,
            title = Res.string.text_authors,
            onShow = { onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Media.Authors)) },
            onNavigate = { onNavigate(Screen.Person(it.toLong())) }
        )
    }

    RelatedFull(
        related = manga.related,
        chronology = manga.chronology,
        franchise = manga.franchiseList,
        isVisible = state.dialogState is BaseDialogState.Media.Related,
        onNavigate = onNavigate,
        onHide = { onEvent(ContentDetailEvent.ToggleDialog(null)) }
    )

    ProfilesFull(
        list = if (state.showCharacters) manga.charactersAll else manga.personAll,
        isVisible = state.showCharacters || state.showAuthors,
        title = stringResource(if (state.showCharacters) Res.string.text_characters else Res.string.text_authors),
        state = if (state.showCharacters) charactersState else authorsState,
        onHide = { onEvent(ContentDetailEvent.ToggleDialog(null)) },
        onNavigate = {
            onNavigate(
                if (state.showCharacters) Screen.Character(it)
                else Screen.Person(it.toLong())
            )
        }
    )

    SimilarFull(
        listState = similarState,
        isVisible = state.dialogState is BaseDialogState.Media.Similar,
        list = manga.similar,
        onNavigate = { onNavigate(Screen.Manga(it)) },
        onHide = { onEvent(ContentDetailEvent.ToggleDialog(null)) },
    )

    Statistics(
        statistics = manga.stats,
        isVisible = state.dialogState is BaseDialogState.Media.Stats,
        onHide = { onEvent(ContentDetailEvent.ToggleDialog(null)) },
    )

    DialogPoster(
        link = manga.poster,
        isVisible = state.dialogState is BaseDialogState.Poster,
        onClose = { onEvent(ContentDetailEvent.ToggleDialog(null)) },
    )

    when (state.dialogState) {
        BaseDialogState.Media.Rate -> DialogEditRate(
            state = newRate,
            type = LinkedType.MANGA,
            isExists = rate != null,
            onEvent = rateModel::onEvent,
            onDismiss = { onEvent(ContentDetailEvent.ToggleDialog(null)) },
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

        BaseDialogState.Sheet -> BottomSheet(
            url = manga.url,
            canShowLinks = manga.links.isNotEmpty(),
            onEvent = onEvent
        )

        BaseDialogState.Media.Links -> LinksSheet(manga.links) {
            onEvent(ContentDetailEvent.ToggleDialog(BaseDialogState.Sheet))
        }

        else -> Unit
    }
}