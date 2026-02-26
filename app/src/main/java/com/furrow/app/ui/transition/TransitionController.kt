package com.furrow.app.ui.transition

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

val LocalTransitionController = staticCompositionLocalOf<TransitionController> {
    error("No TransitionController provided")
}

@Stable
class TransitionController {
    var isTransitioning by mutableStateOf(false)
        private set

    private var pendingNavigation: (() -> Unit)? = null

    fun navigateWithTransition(action: () -> Unit) {
        if (isTransitioning) return
        isTransitioning = true
        pendingNavigation = action
    }

    fun onWipeInComplete() {
        pendingNavigation?.invoke()
        pendingNavigation = null
    }

    fun onTransitionComplete() {
        isTransitioning = false
    }
}
