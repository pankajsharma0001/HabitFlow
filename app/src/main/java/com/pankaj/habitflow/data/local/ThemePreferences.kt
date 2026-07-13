package com.pankaj.habitflow.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pankaj.habitflow.presentation.theme.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val themeKey = stringPreferencesKey("theme_mode")
    private val eveningReminderKey = booleanPreferencesKey("evening_reminder_enabled")
    private val eveningTimeKey = intPreferencesKey("evening_reminder_time")
    private val syncEnabledKey = booleanPreferencesKey("sync_enabled")
    private val lastSyncKey = longPreferencesKey("last_sync_timestamp")
    private val onboardingCompletedKey = booleanPreferencesKey("onboarding_completed")

    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val name = preferences[themeKey] ?: ThemeMode.SYSTEM.name
        try {
            ThemeMode.valueOf(name)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }

    val eveningReminderFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[eveningReminderKey] ?: false
    }

    val eveningReminderTimeFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[eveningTimeKey] ?: 1200
    }

    val syncEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[syncEnabledKey] ?: false
    }

    val lastSyncTimestampFlow: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[lastSyncKey] ?: 0L
    }

    val onboardingCompletedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[onboardingCompletedKey] ?: false
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = mode.name
        }
    }

    suspend fun setEveningReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[eveningReminderKey] = enabled
        }
    }

    suspend fun setEveningReminderTime(timeMinutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[eveningTimeKey] = timeMinutes
        }
    }

    suspend fun setSyncEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[syncEnabledKey] = enabled
        }
    }

    suspend fun setLastSyncTimestamp(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[lastSyncKey] = timestamp
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[onboardingCompletedKey] = completed
        }
    }
}
