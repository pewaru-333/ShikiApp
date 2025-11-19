package org.application.shikiapp.utils.extensions

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.annotations.ApolloExperimental
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.api.http.get
import com.apollographql.apollo.exception.DefaultApolloException
import com.apollographql.apollo.network.http.HttpInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.application.shikiapp.network.cache.CacheEntry
import org.application.shikiapp.network.cache.CacheManager

suspend inline fun <reified T> HttpClient.requestWithCache(
    cacheKey: String,
    crossinline requestBlock: suspend (etag: String?) -> HttpResponse
): T {
    val cachedEntry = CacheManager.get<T>(cacheKey)
    val response = requestBlock(cachedEntry?.etag)

    when (response.status) {
        HttpStatusCode.NotModified -> if (cachedEntry != null) {
            return cachedEntry.data
        } else {
            throw IllegalStateException("304 Not Modified, but no cache for ($cacheKey)!")
        }

        HttpStatusCode.OK -> {
            val data = response.body<T>()
            val newEtag = response.headers[HttpHeaders.ETag]

            CacheManager.put(cacheKey, CacheEntry(data, newEtag))

            return data
        }

        else -> {
            throw ClientRequestException(response, response.bodyAsText())
        }
    }
}

suspend inline fun <reified T> HttpClient.requestWithCache(
    cacheKey: String,
    url: String,
    crossinline block: HttpRequestBuilder.() -> Unit = {}
): T = requestWithCache(cacheKey) { etag ->
    get(url) {
        block()

        if (etag != null) {
            headers.append(HttpHeaders.IfNoneMatch, etag)
        }
    }
}

@OptIn(ApolloExperimental::class)
fun <D : Query.Data, T> ApolloClient.cachedQueryFlow(
    query: Query<D>,
    cacheKey: String,
    mapData: (D) -> T?
): Flow<T> = flow {
    val cachedEntry = CacheManager.get<T>(cacheKey)

    if (cachedEntry != null) {
        emit(cachedEntry.data)
    }

    val response = query(query)
        .addHttpHeader(HttpHeaders.IfNoneMatch, cachedEntry?.etag.orEmpty())
        .execute()

    val httpInfo = response.executionContext[HttpInfo] ?: throw DefaultApolloException()

    if (response.data != null) {
        val newEtag = httpInfo.headers.get(HttpHeaders.ETag)

        if (cachedEntry?.etag != newEtag) {
            val newData = mapData(response.data!!)

            if (newData != null) {
                CacheManager.put(cacheKey, CacheEntry(newData, newEtag))
                emit(newData)
            }
        }
    }
}