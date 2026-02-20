package com.furrow.app.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.components.PrimaryButton
import com.furrow.app.ui.components.SecondaryButton
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary

@Composable
fun RequestNotificationPermission(onResult: (Boolean) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            onResult(granted)
        }
        LaunchedEffect(Unit) { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }
    } else {
        LaunchedEffect(Unit) { onResult(true) }
    }
}

fun shouldAskNotificationPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS,
    ) != PackageManager.PERMISSION_GRANTED
}

@Composable
fun NotificationPermissionScreen(
    onEnableClick: () -> Unit,
    onSkipClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Panel {
            Text("Enable reminders", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Text(
                "Allow notifications to receive inspection, harvest, and egg logging reminders.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
            PrimaryButton(
                text = "Enable notifications",
                onClick = onEnableClick,
                modifier = Modifier.fillMaxWidth(),
            )
            SecondaryButton(
                text = "Maybe later",
                onClick = onSkipClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
