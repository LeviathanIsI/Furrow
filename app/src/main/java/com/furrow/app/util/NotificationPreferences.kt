package com.furrow.app.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    val beeReminders: Flow<Boolean> = dataStore.data.map { it[BEE_REMINDERS] ?: true }
    val poultryReminders: Flow<Boolean> = dataStore.data.map { it[POULTRY_REMINDERS] ?: true }
    val gardenReminders: Flow<Boolean> = dataStore.data.map { it[GARDEN_REMINDERS] ?: true }
    val notificationPromptShown: Flow<Boolean> = dataStore.data.map { it[NOTIFICATION_PROMPT_SHOWN] ?: false }

    suspend fun setBeeReminders(enabled: Boolean) {
        dataStore.edit { it[BEE_REMINDERS] = enabled }
    }

    suspend fun setPoultryReminders(enabled: Boolean) {
        dataStore.edit { it[POULTRY_REMINDERS] = enabled }
    }

    suspend fun setGardenReminders(enabled: Boolean) {
        dataStore.edit { it[GARDEN_REMINDERS] = enabled }
    }

    suspend fun setNotificationPromptShown(shown: Boolean) {
        dataStore.edit { it[NOTIFICATION_PROMPT_SHOWN] = shown }
    }

    companion object {
        val BEE_REMINDERS = booleanPreferencesKey("bee_reminders")
        val POULTRY_REMINDERS = booleanPreferencesKey("poultry_reminders")
        val GARDEN_REMINDERS = booleanPreferencesKey("garden_reminders")
        val NOTIFICATION_PROMPT_SHOWN = booleanPreferencesKey("notification_prompt_shown")
    }
}
