package org.application.shikiapp.shared.utils.data.preferences

import kotlinx.coroutines.flow.Flow

interface IPreferences {
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
    fun getInt(key: String, defaultValue: Int): Int
    fun putInt(key: String, value: Int)
    fun getLong(key: String, defaultValue: Long): Long
    fun putLong(key: String, value: Long)
    fun getString(key: String, defaultValue: String): String
    fun putString(key: String, value: String)

    fun <E : Enum<E>> putEnum(key: String, value: E) {
        putString(key, value.name)
    }

    fun remove(key: String)

    fun flow(key: String): Flow<Unit>
}