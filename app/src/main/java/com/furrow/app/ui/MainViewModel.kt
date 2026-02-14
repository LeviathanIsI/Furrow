package com.furrow.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furrow.app.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userProfileRepository: UserProfileRepository,
) : ViewModel() {

    val hasProfile: StateFlow<Boolean?> = userProfileRepository.getProfile()
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
