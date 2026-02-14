package com.furrow.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furrow.app.data.ZoneLookup
import com.furrow.app.data.local.entity.UserProfile
import com.furrow.app.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: UserProfileRepository,
) : ViewModel() {

    val profile: StateFlow<UserProfile?> = repository.getProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateZipCode(zipCode: String) {
        val newProfile = ZoneLookup.deriveProfile(zipCode) ?: return
        viewModelScope.launch {
            repository.saveProfile(newProfile)
        }
    }
}
