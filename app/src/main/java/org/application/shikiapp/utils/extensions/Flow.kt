package org.application.shikiapp.utils.extensions

import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest


@OptIn(ExperimentalCoroutinesApi::class)
fun <T : Operation.Data, D> Flow<ApolloResponse<T>>.mapToResult(transform: (T) -> D) =
    mapLatest { response ->
        when {
            response.hasErrors() -> null
            response.data == null -> null
            else -> transform(response.data as T)
        }
    }.filterNotNull()