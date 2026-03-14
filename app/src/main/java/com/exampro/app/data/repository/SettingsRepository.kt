package com.exampro.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.exampro.app.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.DATASTORE_NAME)

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val BASE_URL_KEY = stringPreferencesKey("base_url")

    val baseUrl: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[BASE_URL_KEY] ?: Constants.BASE_URL
    }

    suspend fun updateBaseUrl(newBaseUrl: String) {
        context.dataStore.edit { preferences ->
            preferences[BASE_URL_KEY] = if (newBaseUrl.endsWith("/")) newBaseUrl else "$newBaseUrl/"
        }
    }

    suspend fun resetBaseUrl() {
        context.dataStore.edit { preferences ->
            preferences.remove(BASE_URL_KEY)
        }
    }
}
