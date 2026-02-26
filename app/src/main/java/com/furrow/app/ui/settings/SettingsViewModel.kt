package com.furrow.app.ui.settings

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.furrow.app.data.ZoneLookup
import com.furrow.app.ui.FurrowViewModel
import com.furrow.app.data.local.entity.UserModulePreference
import com.furrow.app.data.local.entity.UserProfile
import com.furrow.app.data.repository.ModulePreferenceRepository
import com.furrow.app.data.repository.UserProfileRepository
import com.furrow.app.util.BackupManager
import com.furrow.app.util.NotificationPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: UserProfileRepository,
    private val notificationPreferences: NotificationPreferences,
    private val modulePreferenceRepository: ModulePreferenceRepository,
    private val backupManager: BackupManager,
) : FurrowViewModel() {

    private val _backupResult = MutableStateFlow<String?>(null)
    val backupResult: StateFlow<String?> = _backupResult.asStateFlow()
    fun clearBackupResult() { _backupResult.value = null }

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
        safeLaunch {
            repository.saveProfile(newProfile)
        }
    }

    fun setBeeReminders(enabled: Boolean) {
        safeLaunch { notificationPreferences.setBeeReminders(enabled) }
    }

    fun setPoultryReminders(enabled: Boolean) {
        safeLaunch { notificationPreferences.setPoultryReminders(enabled) }
    }

    fun setGardenReminders(enabled: Boolean) {
        safeLaunch { notificationPreferences.setGardenReminders(enabled) }
    }

    fun setModuleEnabled(moduleName: String, enabled: Boolean) {
        safeLaunch {
            modulePreferenceRepository.setEnabled(moduleName, enabled)
        }
    }

    fun updateTimezone(timezone: String) {
        safeLaunch {
            val current = profile.value ?: return@safeLaunch
            repository.saveProfile(current.copy(timezone = timezone))
        }
    }

    fun exportBackup(uri: Uri) {
        safeLaunch {
            val success = backupManager.exportBackup(uri)
            _backupResult.value = if (success) "Backup exported successfully" else "Export failed"
        }
    }

    fun importBackup(uri: Uri) {
        safeLaunch {
            val success = backupManager.importBackup(uri)
            _backupResult.value = if (success) "Backup imported — restart app to apply" else "Import failed"
        }
    }
}
