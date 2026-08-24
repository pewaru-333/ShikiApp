package org.application.shikiapp.shared.utils.extensions

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.annotations.ApolloExperimental
import com.apollographql.apollo.api.Query
import kotlinx.coroutines.flow.flow

@OptIn(ApolloExperimental::class)
fun <D : Query.Data, T : Any> ApolloClient.cachedFlow(query: Query<D>, mapData: (D) -> T?) = flow {
    try {
        query(query).toFlow().collect {
            if (it.hasErrors()) {
                throw Exception(it.exception)
            }

            it.data?.let { response ->
                mapData(response)?.let { data ->
                    emit(data)
                }
            }
        }
    } catch (e: Exception) {
        throw e
    }
}