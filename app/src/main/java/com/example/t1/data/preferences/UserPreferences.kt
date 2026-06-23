package com.example.t1.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "t1_settings")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_DISPLAY_NAME = stringPreferencesKey("display_name")
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val KEY_FOCUS_SCORE = intPreferencesKey("focus_score")
        private val KEY_IS_SIGNED_IN = booleanPreferencesKey("is_signed_in")
    }

    val userIdFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_USER_ID]
    }

    val usernameFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_USERNAME]
    }

    val displayNameFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_DISPLAY_NAME]
    }

    val onboardingCompletedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_ONBOARDING_COMPLETED] ?: false
    }

    val focusScoreFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[KEY_FOCUS_SCORE] ?: 50
    }

    val isSignedInFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_IS_SIGNED_IN] ?: false
    }

    suspend fun saveUserSession(userId: String, username: String?, displayName: String?) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_ID] = userId
            preferences[KEY_IS_SIGNED_IN] = true
            if (username != null) {
                preferences[KEY_USERNAME] = username
            }
            if (displayName != null) {
                preferences[KEY_DISPLAY_NAME] = displayName
            }
        }
    }

    suspend fun saveUsernameAndDisplayName(username: String, displayName: String?) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USERNAME] = username
            if (displayName != null) {
                preferences[KEY_DISPLAY_NAME] = displayName
            } else {
                preferences.remove(KEY_DISPLAY_NAME)
            }
        }
    }

    suspend fun saveOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun saveFocusScore(score: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_FOCUS_SCORE] = score
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_USER_ID)
            preferences.remove(KEY_USERNAME)
            preferences.remove(KEY_DISPLAY_NAME)
            preferences[KEY_ONBOARDING_COMPLETED] = false
            preferences[KEY_FOCUS_SCORE] = 50
            preferences[KEY_IS_SIGNED_IN] = false
        }
    }
}
