package com.furrow.app.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.furrow.app.data.local.entity.PlantInfo
import com.furrow.app.data.local.entity.PlantVariety
import com.furrow.app.data.local.entity.PlantingWindow
import com.furrow.app.ui.components.AppChip
import com.furrow.app.ui.components.AppSectionHeader
import com.furrow.app.ui.theme.AppRadius
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.BorderVisible
import com.furrow.app.ui.theme.GardenGlow
import com.furrow.app.ui.theme.Graphite
import com.furrow.app.ui.theme.StatusBad
import com.furrow.app.ui.theme.StatusWarn
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextSecondary
import com.furrow.app.ui.theme.TextTertiary

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
        shape = RoundedCornerShape(topStart = AppRadius.sheet, topEnd = AppRadius.sheet),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .size(width = 32.dp, height = 4.dp)
                    .background(BorderVisible, RoundedCornerShape(2.dp)),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // -- Overview --
            Text(
                plant.name,
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                InfoChip(plant.category.replaceFirstChar { it.uppercase() }, GardenGlow)
                if (plant.perennial) InfoChip("Perennial", StatusWarn)
                if (plant.frostTolerant) InfoChip("Frost Tolerant", GardenGlow.copy(alpha = 0.7f))
                plant.rotationGroup?.let { InfoChip("Rotation: $it", TextSecondary) }
            }

            SectionDivider()
            PlantDetailRow("Hardiness Zone", "${plant.minZone}-${plant.maxZone}")
            PlantDetailRow("Days to Harvest", "${plant.daysToHarvestMin}-${plant.daysToHarvestMax} days")
            PlantDetailRow("Sun", plant.sunRequirement.replaceFirstChar { it.uppercase() })
            PlantDetailRow("Water", plant.waterFrequency.replaceFirstChar { it.uppercase() })
            if (plant.containerSuitable) {
                PlantDetailRow("Container", "Yes (min ${plant.containerMinGallons} gal)")
            }

            // -- Starting --
            val hasStarting = plant.indoorStartWeeksBefore != null || plant.minSoilTempF != null ||
                plant.seedSoakHours != null || plant.scarification == true || plant.startNotes != null ||
                plant.sowMethod != null || plant.plantingDepthInches != null ||
                plant.germinationDaysMin != null
            if (hasStarting) {
                SectionHeader("STARTING")
                plant.sowMethod?.let {
                    PlantDetailRow("Sow Method", it.replace("_", " ").replaceFirstChar { c -> c.uppercase() })
                }
                plant.indoorStartWeeksBefore?.let {
                    PlantDetailRow("Indoor Start", "$it weeks before last frost")
                }
                plant.plantingDepthInches?.let {
                    PlantDetailRow("Planting Depth", if (it == 0f) "Surface sow" else "$it in")
                }
                plant.minSoilTempF?.let { PlantDetailRow("Min Soil Temp", "${it}\u00B0F") }
                plant.seedSoakHours?.let { PlantDetailRow("Seed Soak", "$it hours") }
                if (plant.scarification == true) PlantDetailRow("Scarification", "Recommended")
                plant.germinationDaysMin?.let { min ->
                    val max = plant.germinationDaysMax
                    if (max != null && max != min) PlantDetailRow("Germination", "$min-$max days")
                    else PlantDetailRow("Germination", "$min days")
                }
                plant.startNotes?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            // -- Spacing --
            val hasSpacing = plant.spacingInches != null || plant.plantHeight != null
            if (hasSpacing) {
                SectionHeader("SPACING")
                plant.spacingInches?.let { spacing ->
                    val row = plant.rowSpacingInches
                    if (row != null) PlantDetailRow("Spacing", "${spacing} in apart, ${row} in rows")
                    else PlantDetailRow("Spacing", "${spacing} in apart")
                }
                plant.plantHeight?.let { PlantDetailRow("Height", it) }
                plant.thinningNotes?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            // -- Growing --
            val hasGrowing = plant.waterInchesPerWeek != null || plant.minTempF != null ||
                plant.maxTempF != null || plant.stakingRequired == true || plant.mulchRecommended == true ||
                plant.pruningNotes != null || plant.heatTips != null || plant.coldTips != null
            if (hasGrowing) {
                SectionHeader("GROWING")
                plant.waterInchesPerWeek?.let { PlantDetailRow("Water Needs", "$it in/week") }
                plant.minTempF?.let { min ->
                    val max = plant.maxTempF
                    if (max != null) PlantDetailRow("Temp Range", "${min}\u00B0F \u2013 ${max}\u00B0F")
                    else PlantDetailRow("Min Temp", "${min}\u00B0F")
                }
                if (plant.stakingRequired == true) PlantDetailRow("Staking", "Required")
                if (plant.mulchRecommended == true) PlantDetailRow("Mulch", "Recommended")
                plant.pruningNotes?.let { PlantDetailRow("Pruning", it) }
                plant.heatTips?.let {
                    Text("Heat: $it", style = MaterialTheme.typography.bodySmall, color = StatusWarn)
                }
                plant.coldTips?.let {
                    Text("Cold: $it", style = MaterialTheme.typography.bodySmall, color = GardenGlow.copy(alpha = 0.7f))
                }
            }

            // -- Feeding --
            val hasFeeding = plant.fertilizerNeeds != null || plant.fertilizerType != null ||
                plant.fertilizerFrequency != null
            if (hasFeeding) {
                SectionHeader("FEEDING")
                plant.fertilizerNeeds?.let { PlantDetailRow("Needs", it.replaceFirstChar { c -> c.uppercase() }) }
                plant.fertilizerType?.let { PlantDetailRow("Type", it) }
                plant.fertilizerFrequency?.let { PlantDetailRow("Frequency", it) }
            }

            // -- Harvest --
            val hasHarvest = plant.harvestIndicators != null || plant.harvestFrequency != null ||
                plant.yieldPerPlant != null || plant.storageNotes != null ||
                plant.harvestMethod != null
            if (hasHarvest) {
                SectionHeader("HARVEST")
                plant.harvestMethod?.let {
                    PlantDetailRow("Method", it.replace("_", " ").replaceFirstChar { c -> c.uppercase() })
                }
                plant.harvestIndicators?.let { PlantDetailRow("Indicators", it) }
                plant.harvestFrequency?.let { PlantDetailRow("Frequency", it) }
                plant.yieldPerPlant?.let { PlantDetailRow("Yield/Plant", it) }
                plant.storageNotes?.let { PlantDetailRow("Storage", it) }
            }

            // -- Succession & Rotation --
            val hasSuccession = plant.canSuccessionPlant == true || plant.successionPlantingDays != null ||
                plant.rotationNotes != null
            if (hasSuccession) {
                SectionHeader("SUCCESSION & ROTATION")
                if (plant.canSuccessionPlant == true) {
                    val days = plant.successionPlantingDays
                    if (days != null) PlantDetailRow("Succession", "Every $days days")
                    else PlantDetailRow("Succession", "Yes")
                }
                plant.rotationGroup?.let { PlantDetailRow("Rotation Group", it) }
                plant.rotationNotes?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            // -- Problems --
            val hasProblems = plant.commonPests != null || plant.commonDiseases != null ||
                plant.pestNotes != null
            if (hasProblems) {
                SectionHeader("COMMON PROBLEMS")
                plant.commonPests?.let { pests ->
                    Text("Pests", style = MaterialTheme.typography.labelSmall, color = StatusBad)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        pests.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { pest ->
                            InfoChip(pest, StatusBad)
                        }
                    }
                }
                plant.commonDiseases?.let { diseases ->
                    Text("Diseases", style = MaterialTheme.typography.labelSmall, color = StatusBad)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        diseases.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { disease ->
                            InfoChip(disease, StatusBad)
                        }
                    }
                }
                plant.pestNotes?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            // -- Companions --
            if (plant.companionPlants.isNotBlank()) {
                SectionHeader("COMPANION PLANTS")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    plant.companionPlants.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { name ->
                        InfoChip(name, GardenGlow)
                    }
                }
            }

            if (plant.incompatiblePlants.isNotBlank()) {
                Text(
                    "AVOID PLANTING WITH",
                    style = MaterialTheme.typography.labelSmall,
                    color = StatusBad,
                    letterSpacing = 1.sp,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    plant.incompatiblePlants.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { name ->
                        InfoChip(name, StatusBad)
                    }
                }
            }

            // -- Varieties --
            if (varieties.isNotEmpty()) {
                SectionHeader("VARIETIES")
                varieties.forEach { v ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            v.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f),
                        )
                        val dtm = listOfNotNull(v.daysToHarvestMin, v.daysToHarvestMax)
                            .joinToString("-")
                        if (dtm.isNotBlank()) {
                            Text(
                                "${dtm}d",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                            )
                        }
                    }
                    v.description?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }

            // -- Planting Windows --
            if (plantingWindows.isNotEmpty()) {
                SectionHeader("PLANTING WINDOWS")
                plantingWindows.forEach { w ->
                    val months = listOf(
                        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
                    )
                    val startName = months.getOrElse(w.startMonth - 1) { "?" }
                    val endName = months.getOrElse(w.endMonth - 1) { "?" }
                    PlantDetailRow(
                        w.method.replaceFirstChar { it.uppercase() },
                        "$startName \u2013 $endName",
                    )
                }
            }

            // -- Notes --
            plant.notes?.let {
                SectionDivider()
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    SectionDivider()
    AppSectionHeader(title = title)
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(color = BorderSubtle)
}

@Composable
private fun InfoChip(text: String, color: androidx.compose.ui.graphics.Color) {
    AppChip(
        text = text,
        selected = color == GardenGlow,
        accentColor = color,
    )
}

@Composable
internal fun PlantDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
        )
    }
}
