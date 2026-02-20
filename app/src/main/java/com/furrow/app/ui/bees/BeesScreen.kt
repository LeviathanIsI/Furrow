package com.furrow.app.ui.bees

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.furrow.app.ui.theme.BeeGlow
import com.furrow.app.ui.theme.TextSecondary

@Composable
fun BeesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Bees",
            style = MaterialTheme.typography.headlineLarge,
            color = BeeGlow,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Manage your hives and inspections",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
        )
    }
}
