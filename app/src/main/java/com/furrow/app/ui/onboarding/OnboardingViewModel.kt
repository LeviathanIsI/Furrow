package com.furrow.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furrow.app.data.ZoneLookup
import com.furrow.app.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: UserProfileRepository,
) : ViewModel() {

    fun saveProfile(zipCode: String) {
        val profile = ZoneLookup.deriveProfile(zipCode) ?: return
        viewModelScope.launch {
            repository.saveProfile(profile)
        }
    }
}
