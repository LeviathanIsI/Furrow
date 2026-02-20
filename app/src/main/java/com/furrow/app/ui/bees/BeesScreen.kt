package com.furrow.app.ui.bees

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.furrow.app.ui.components.Panel

@Composable
fun BeesScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Panel {
            Text("Bees", style = MaterialTheme.typography.headlineMedium)
            Text("Hive tracking workspace", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
