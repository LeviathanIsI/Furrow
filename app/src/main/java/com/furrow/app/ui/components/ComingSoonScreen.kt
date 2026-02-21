package com.furrow.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.TextMuted
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary

@Composable
fun ComingSoonScreen(
    title: String,
    icon: ImageVector = Icons.Outlined.Construction,
    accentColor: androidx.compose.ui.graphics.Color = TextMuted,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = accentColor,
        )
        Spacer(modifier = Modifier.height(AppSpacing.md))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
        )
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        Text(
            text = "Coming soon",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
    }
}
