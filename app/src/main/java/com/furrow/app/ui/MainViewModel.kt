package com.furrow.app.ui

import androidx.lifecycle.viewModelScope
import com.furrow.app.data.FurrowModule
import com.furrow.app.ui.FurrowViewModel
import com.furrow.app.data.repository.ModulePreferenceRepository
import com.furrow.app.data.repository.UserProfileRepository
import com.furrow.app.util.NotificationPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userProfileRepository: UserProfileRepository,
    private val notificationPreferences: NotificationPreferences,
    modulePreferenceRepository: ModulePreferenceRepository,
) : FurrowViewModel() {

    val hasProfile: StateFlow<Boolean?> = userProfileRepository.getProfile()
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val notificationPromptShown: StateFlow<Boolean> = notificationPreferences.notificationPromptShown
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    private val defaultKeys = FurrowModule.entries
        .filter { it.enabledByDefault }
        .map { it.key }
        .toSet()

    /** Set of enabled module keys (unordered). */
    val enabledModuleKeys: StateFlow<Set<String>> = modulePreferenceRepository.getEnabled()
        .map { prefs -> prefs.map { it.moduleName }.toSet() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, defaultKeys)

    /** Enabled module keys in display order for bottom nav. */
    val enabledModulesOrdered: StateFlow<List<String>> = modulePreferenceRepository.getEnabled()
        .map { prefs -> prefs.sortedBy { it.displayOrder }.map { it.moduleName } }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            FurrowModule.entries.filter { it.enabledByDefault }.sortedBy { it.displayOrder }.map { it.key },
        )

    fun onNotificationPromptDone() {
        safeLaunch {
            notificationPreferences.setNotificationPromptShown(true)
        }
    }
}
