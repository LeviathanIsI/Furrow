package com.furrow.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furrow.app.data.ZoneLookup
import com.furrow.app.data.local.entity.UserModulePreference
import com.furrow.app.data.local.entity.UserProfile
import com.furrow.app.data.repository.ModulePreferenceRepository
import com.furrow.app.data.repository.UserProfileRepository
import com.furrow.app.util.NotificationPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: UserProfileRepository,
    private val notificationPreferences: NotificationPreferences,
    private val modulePreferenceRepository: ModulePreferenceRepository,
) : ViewModel() {

    val profile: StateFlow<UserProfile?> = repository.getProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val beeReminders: StateFlow<Boolean> = notificationPreferences.beeReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val poultryReminders: StateFlow<Boolean> = notificationPreferences.poultryReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val gardenReminders: StateFlow<Boolean> = notificationPreferences.gardenReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val modulePreferences: StateFlow<List<UserModulePreference>> =
        modulePreferenceRepository.getAll()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateZipCode(zipCode: String) {
        val newProfile = ZoneLookup.deriveProfile(zipCode) ?: return
        viewModelScope.launch {
            repository.saveProfile(newProfile)
        }
    }

    fun setBeeReminders(enabled: Boolean) {
        viewModelScope.launch { notificationPreferences.setBeeReminders(enabled) }
    }

    fun setPoultryReminders(enabled: Boolean) {
        viewModelScope.launch { notificationPreferences.setPoultryReminders(enabled) }
    }

    fun setGardenReminders(enabled: Boolean) {
        viewModelScope.launch { notificationPreferences.setGardenReminders(enabled) }
    }

    fun setModuleEnabled(moduleName: String, enabled: Boolean) {
        viewModelScope.launch {
            modulePreferenceRepository.setEnabled(moduleName, enabled)
        }
    }
}
