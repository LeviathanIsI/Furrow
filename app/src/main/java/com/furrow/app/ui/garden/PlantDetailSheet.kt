package com.furrow.app.ui.garden

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.PlantVariety
import com.furrow.app.data.local.entity.PlantingWindow
import com.furrow.app.ui.components.AppSectionHeader
import com.furrow.app.ui.components.Tag
import com.furrow.app.ui.theme.AppRadius
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.Graphite
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun PlantDetailSheet(
    plant: PlantInfo,
    varieties: List<PlantVariety> = emptyList(),
    plantingWindows: List<PlantingWindow> = emptyList(),
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Graphite,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(
            topStart = AppRadius.md,
            topEnd = AppRadius.md,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            Text(
                text = plant.name,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
            )
            Text(
                text = "Zone ${plant.minZone}-${plant.maxZone} • ${plant.daysToHarvestMin}-${plant.daysToHarvestMax} days",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                Tag(text = plant.category.replaceFirstChar { it.uppercase() })
                Tag(text = plant.sunRequirement.replaceFirstChar { it.uppercase() })
                if (plant.perennial) Tag(text = "Perennial")
                if (plant.containerSuitable) Tag(text = "Container")
            }

            HorizontalDivider(color = BorderSubtle)
            PlantDetailRow("Water", plant.waterFrequency.replaceFirstChar { it.uppercase() })
            plant.spacingInches?.let { PlantDetailRow("Spacing", "$it in") }
            plant.fertilizerType?.let { PlantDetailRow("Feed", it) }
            plant.harvestMethod?.let { PlantDetailRow("Harvest", it.replace('_', ' ')) }

            if (plant.companionPlants.isNotBlank()) {
                AppSectionHeader("Companion plants")
                Text(plant.companionPlants, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
            if (plant.incompatiblePlants.isNotBlank()) {
                AppSectionHeader("Avoid with")
                Text(plant.incompatiblePlants, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }

            if (varieties.isNotEmpty()) {
                AppSectionHeader("Varieties")
                varieties.forEachIndexed { index, variety ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(variety.name, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                        val days = listOfNotNull(variety.daysToHarvestMin, variety.daysToHarvestMax).joinToString("-")
                        if (days.isNotBlank()) {
                            Text("${days}d", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    if (index != varieties.lastIndex) HorizontalDivider(color = BorderSubtle)
                }
            }

            if (plantingWindows.isNotEmpty()) {
                AppSectionHeader("Planting windows")
                plantingWindows.forEach { window ->
                    PlantDetailRow(
                        label = window.method.replaceFirstChar { it.uppercase() },
                        value = "Month ${window.startMonth} - ${window.endMonth}",
                    )
                }
            }

            plant.notes?.takeIf { it.isNotBlank() }?.let {
                AppSectionHeader("Notes")
                Text(it, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(AppSpacing.md))
        }
    }
}

@Composable
internal fun PlantDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
    }
}
