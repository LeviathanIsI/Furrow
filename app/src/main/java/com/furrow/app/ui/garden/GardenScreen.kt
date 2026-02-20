package com.furrow.app.ui.garden

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
fun GardenScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Panel {
            Text("Garden", style = MaterialTheme.typography.headlineMedium)
            Text("Bed and crop tracking workspace", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
