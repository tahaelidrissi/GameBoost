package com.gameboost.frontend.utils

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("tokens")

class TokenManager(private val context: Context) {
    companion object {
        private val JWT_TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    fun getToken(): String? {
        return try {
            context.dataStore.data.map { preferences ->
                preferences[JWT_TOKEN_KEY]
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveToken(token: String, role: String, userId: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[JWT_TOKEN_KEY] = token
                preferences[USER_ROLE_KEY] = role
                preferences[USER_ID_KEY] = userId
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun clearToken() {
        try {
            context.dataStore.edit { preferences ->
                preferences.remove(JWT_TOKEN_KEY)
                preferences.remove(USER_ROLE_KEY)
                preferences.remove(USER_ID_KEY)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getTokenFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[JWT_TOKEN_KEY]
        }
    }

    fun getUserRoleFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ROLE_KEY]
        }
    }
}
