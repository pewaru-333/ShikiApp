@file:OptIn(ExperimentalMaterial3Api::class)

package org.application.shikiapp.shared.ui.templates

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.extensions.pairwise
import org.jetbrains.compose.resources.stringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.text_search
import shikiapp.composeapp.generated.resources.vector_close
import shikiapp.composeapp.generated.resources.vector_search

@Composable
fun SearchAppBar(
    search: String,
    onSearch: (String) -> Unit,
    onClear: () -> Unit = { onSearch(BLANK) },
    navigationIcon: @Composable (() -> Unit),
    actions: @Composable (RowScope.() -> Unit)
) {
    val searchState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState(search)

    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text.toString() }
            .pairwise()
            .collectLatest { (old, new) ->
                if (old != new) {
                    onSearch(new)
                }
            }
    }

    LaunchedEffect(search) {
        if (search.isEmpty()) {
            textFieldState.clearText()
        }
    }

    AppBarWithSearch(
        state = searchState,
        navigationIcon = navigationIcon,
        actions = actions,
        inputField = {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchState,
                onSearch = onSearch,
                leadingIcon = { VectorIcon(Res.drawable.vector_search) },
                placeholder = { Text(stringResource(Res.string.text_search)) },
                trailingIcon = {
                    if (search.isNotEmpty()) {
                        IconButton(onClear) { VectorIcon(Res.drawable.vector_close) }
                    }
                }
            )
        }
    )
}