package com.furrow.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furrow.app.data.repository.UserProfileRepository
import com.furrow.app.util.NotificationPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userProfileRepository: UserProfileRepository,
    private val notificationPreferences: NotificationPreferences,
) : ViewModel() {

    val hasProfile: StateFlow<Boolean?> = userProfileRepository.getProfile()
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val notificationPromptShown: StateFlow<Boolean> = notificationPreferences.notificationPromptShown
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun onNotificationPromptDone() {
        viewModelScope.launch {
            notificationPreferences.setNotificationPromptShown(true)
        }
    }
}
