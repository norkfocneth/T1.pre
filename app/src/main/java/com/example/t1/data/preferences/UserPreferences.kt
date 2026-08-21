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

/**
 * DataStore implementation managing non-authentication local application preferences only.
 * Complies with Phase 1 safety requirements by storing no session, identity, or profile data.
 */
@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_THEME = stringPreferencesKey("app_theme")
        private val KEY_LANGUAGE = stringPreferencesKey("app_language")
        private val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val KEY_FOCUS_REMINDERS_ENABLED = booleanPreferencesKey("focus_reminders_enabled")
        private val KEY_TUTORIAL_COMPLETED = booleanPreferencesKey("tutorial_completed")
        private val KEY_TUTORIAL_VERSION = intPreferencesKey("tutorial_version")
        private val KEY_SELECTED_EXAM = stringPreferencesKey("selected_exam_track")
        private val KEY_EXAM_CHANGE_COUNT = intPreferencesKey("exam_change_count_week")
        private val KEY_EXAM_LAST_CHANGED_EPOCH = androidx.datastore.preferences.core.longPreferencesKey("exam_last_changed_epoch")
        private val KEY_PRIVACY_GHOST_MODE = booleanPreferencesKey("privacy_ghost_mode")
        const val CURRENT_TUTORIAL_VERSION = 1
        const val DEFAULT_EXAM = "JEE (Mains & Advanced)"
    }

    /**
     * Emits the chosen theme. Defaults to "system".
     */
    val appThemeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_THEME] ?: "system"
    }

    /**
     * Emits the selected language code. Defaults to "en".
     */
    val appLanguageFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_LANGUAGE] ?: "en"
    }

    /**
     * Emits notification configuration. Defaults to true.
     */
    val notificationsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_NOTIFICATIONS_ENABLED] ?: true
    }

    /**
     * Emits focus reminders preference. Defaults to true.
     */
    val focusRemindersEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_FOCUS_REMINDERS_ENABLED] ?: true
    }

    /**
     * Emits whether the interactive onboarding product tour has been completed.
     */
    val tutorialCompletedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_TUTORIAL_COMPLETED] ?: false
    }

    /**
     * Emits the version of the completed tutorial.
     */
    val tutorialVersionFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[KEY_TUTORIAL_VERSION] ?: 0
    }

    /**
     * Emits the active exam track for Aukaat leaderboard.
     */
    val selectedExamFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_SELECTED_EXAM] ?: DEFAULT_EXAM
    }

    /**
     * Emits the count of exam changes in the current 7-day window.
     */
    val examChangeCountFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        val lastEpoch = preferences[KEY_EXAM_LAST_CHANGED_EPOCH] ?: 0L
        val now = System.currentTimeMillis()
        val sevenDaysMs = 7 * 24 * 60 * 60 * 1000L
        if (now - lastEpoch > sevenDaysMs) {
            0
        } else {
            preferences[KEY_EXAM_CHANGE_COUNT] ?: 0
        }
    }

    /**
     * Emits the last changed epoch millis for exam track.
     */
    val examLastChangedEpochFlow: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[KEY_EXAM_LAST_CHANGED_EPOCH] ?: 0L
    }

    /**
     * Emits whether Privacy Mode (Ghost / Stealth Grind) is enabled.
     */
    val privacyGhostModeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_PRIVACY_GHOST_MODE] ?: false
    }

    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME] = theme
        }
    }

    suspend fun saveLanguage(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LANGUAGE] = languageCode
        }
    }

    suspend fun saveNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun saveFocusRemindersEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_FOCUS_REMINDERS_ENABLED] = enabled
        }
    }

    suspend fun savePrivacyGhostMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_PRIVACY_GHOST_MODE] = enabled
        }
    }

    suspend fun saveSelectedExam(exam: String): Boolean {
        val now = System.currentTimeMillis()
        val sevenDaysMs = 7 * 24 * 60 * 60 * 1000L
        var success = false

        context.dataStore.edit { preferences ->
            val lastEpoch = preferences[KEY_EXAM_LAST_CHANGED_EPOCH] ?: 0L
            val currentCount = if (now - lastEpoch > sevenDaysMs) 0 else (preferences[KEY_EXAM_CHANGE_COUNT] ?: 0)

            if (currentCount < 2) {
                preferences[KEY_SELECTED_EXAM] = exam
                preferences[KEY_EXAM_CHANGE_COUNT] = currentCount + 1
                preferences[KEY_EXAM_LAST_CHANGED_EPOCH] = now
                success = true
            } else {
                success = false
            }
        }
        return success
    }

    suspend fun saveTutorialCompleted(completed: Boolean, version: Int = CURRENT_TUTORIAL_VERSION) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TUTORIAL_COMPLETED] = completed
            preferences[KEY_TUTORIAL_VERSION] = if (completed) version else 0
        }
    }

    suspend fun resetTutorial() {
        context.dataStore.edit { preferences ->
            preferences[KEY_TUTORIAL_COMPLETED] = false
            preferences[KEY_TUTORIAL_VERSION] = 0
        }
    }

    suspend fun clearPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
