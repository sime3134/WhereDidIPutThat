package sime3134.github.io.wheredidiputthat.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsRepository @Inject constructor(private val dataStore: DataStore<Preferences>){

    private val IS_FIRST_RUN_KEY = booleanPreferencesKey("is_first_run")

    private val LAST_LOCATION_ID_KEY = longPreferencesKey("last_location_id")

    fun isFirstRun(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_FIRST_RUN_KEY] ?: true
        }
    }

    suspend fun setFirstRun(isFirstRun: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[IS_FIRST_RUN_KEY] = isFirstRun
            }
        }
    }

    suspend fun resetPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    fun getLastLocationId(): Flow<Long> {
        return dataStore.data.map { preferences ->
            preferences[LAST_LOCATION_ID_KEY] ?: -1
        }
    }

    suspend fun setLastLocationId(id: Long) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[LAST_LOCATION_ID_KEY] = id
            }
        }
    }
}