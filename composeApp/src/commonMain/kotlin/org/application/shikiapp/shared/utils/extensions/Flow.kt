package org.application.shikiapp.shared.utils.extensions

import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull

fun <T> Flow<T>.pairwise(): Flow<Pair<T, T>> = flow {
    var previous: T? = null
    collect { value ->
        previous?.let { emit(it to value) }
        previous = value
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T : Operation.Data, D> Flow<ApolloResponse<T>>.mapToResult(transform: (T) -> D): Flow<D> =
    mapNotNull { response ->
        val data = response.data
        if (response.hasErrors() || data == null) null else transform(data)
    }