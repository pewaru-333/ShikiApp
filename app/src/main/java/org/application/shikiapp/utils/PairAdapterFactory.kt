package org.application.shikiapp.utils

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

object PairAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (type !is ParameterizedType) return null
        if (Pair::class.java != type.rawType) return null

        val listType = Types.newParameterizedType(List::class.java, String::class.java)
        val listAdapter = moshi.adapter<List<String>>(listType)

        return PairAdapter(
            moshi.adapter(type.actualTypeArguments[0]),
            moshi.adapter(type.actualTypeArguments[1]),
            listAdapter
        )
    }

    private class PairAdapter(
        private val firstAdapter: JsonAdapter<Any>,
        private val secondAdapter: JsonAdapter<Any>,
        private val listAdapter: JsonAdapter<List<String>>
    ) : JsonAdapter<Pair<Any, Any>>() {

        override fun fromJson(reader: JsonReader): Pair<Any, Any>? {
            val list = listAdapter.fromJson(reader) ?: return null

            require(list.size == 2) { "Это не тип Pair!: $list" }
            val first = firstAdapter.fromJsonValue(list[0])!!
            val second = secondAdapter.fromJsonValue(list[1])!!
            return first to second
        }

        override fun toJson(writer: JsonWriter, value: Pair<Any, Any>?) {
            if (value != null) {
                writer.beginArray()
                firstAdapter.toJson(writer, value.first)
                secondAdapter.toJson(writer, value.second)
                writer.endArray()
            }
        }
    }
}