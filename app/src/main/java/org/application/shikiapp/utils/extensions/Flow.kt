package org.application.shikiapp.utils.extensions

import androidx.paging.PagingData
import androidx.paging.filter
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

fun <T> Flow<T>.pairwise(): Flow<Pair<T, T>> = flow {
    var previous: T? = null
    collect { value ->
        previous?.let { emit(it to value) }
        previous = value
    }
}

fun <T : Any, K> Flow<PagingData<T>>.distinctBy(selector: (T) -> K): Flow<PagingData<T>> {
    val set = hashSetOf<K>()

    return map { pagingData ->
        pagingData.filter { item ->
            set.add(selector(item))
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T : Operation.Data, D> Flow<ApolloResponse<T>>.mapToResult(transform: (T) -> D) =
    mapLatest { response ->
        when {
            response.hasErrors() -> null
            response.data == null -> null
            else -> transform(response.data as T)
        }
    }.filterNotNull()