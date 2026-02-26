package com.furrow.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class FurrowViewModel : ViewModel() {
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun clearError() { _errorMessage.value = null }
    fun clearSuccess() { _successMessage.value = null }

    protected fun safeLaunch(block: suspend () -> Unit) {
        viewModelScope.launch {
            try { block() }
            catch (e: Exception) {
                _errorMessage.value = e.message ?: "An unexpected error occurred"
            }
        }
    }

    protected fun safeLaunchWithSuccess(successMsg: String, block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
                _successMessage.value = successMsg
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An unexpected error occurred"
            }
        }
    }
}
