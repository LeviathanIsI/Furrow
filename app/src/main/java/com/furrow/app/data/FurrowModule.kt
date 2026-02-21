package com.furrow.app.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.furrow.app.ui.theme.AccentMutedGreen
import com.furrow.app.ui.theme.AnimalsGlow
import com.furrow.app.ui.theme.ComplianceGlow
import com.furrow.app.ui.theme.FinanceGlow
import com.furrow.app.ui.theme.LandGlow
import com.furrow.app.ui.theme.OrchardGlow
import com.furrow.app.ui.theme.PreservationGlow

enum class ModuleCategory(val label: String) {
    GROWING("Growing"),
    ANIMALS("Animals"),
    PRODUCTION("Production"),
    MANAGEMENT("Management"),
}

enum class FurrowModule(
    val key: String,
    val displayName: String,
    val category: ModuleCategory,
    val enabledByDefault: Boolean,
    val displayOrder: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val accentColor: Color,
) {
    BEES(
        key = "bees",
        displayName = "Bees",
        category = ModuleCategory.GROWING,
        enabledByDefault = true,
        displayOrder = 0,
        selectedIcon = Icons.Filled.BugReport,
        unselectedIcon = Icons.Outlined.BugReport,
        accentColor = AccentMutedGreen,
    ),
    POULTRY(
        key = "poultry",
        displayName = "Poultry",
        category = ModuleCategory.ANIMALS,
        enabledByDefault = true,
        displayOrder = 1,
        selectedIcon = Icons.Filled.Egg,
        unselectedIcon = Icons.Outlined.Egg,
        accentColor = AccentMutedGreen,
    ),
    GARDEN(
        key = "garden",
        displayName = "Garden",
        category = ModuleCategory.GROWING,
        enabledByDefault = true,
        displayOrder = 2,
        selectedIcon = Icons.Filled.Grass,
        unselectedIcon = Icons.Outlined.Grass,
        accentColor = AccentMutedGreen,
    ),
    ANIMALS(
        key = "animals",
        displayName = "Animals",
        category = ModuleCategory.ANIMALS,
        enabledByDefault = false,
        displayOrder = 3,
        selectedIcon = Icons.Filled.Pets,
        unselectedIcon = Icons.Outlined.Pets,
        accentColor = AnimalsGlow,
    ),
    ORCHARD(
        key = "orchard",
        displayName = "Orchard",
        category = ModuleCategory.GROWING,
        enabledByDefault = false,
        displayOrder = 4,
        selectedIcon = Icons.Filled.Forest,
        unselectedIcon = Icons.Outlined.Forest,
        accentColor = OrchardGlow,
    ),
    PRESERVATION(
        key = "preservation",
        displayName = "Preservation",
        category = ModuleCategory.PRODUCTION,
        enabledByDefault = false,
        displayOrder = 5,
        selectedIcon = Icons.Filled.Kitchen,
        unselectedIcon = Icons.Outlined.Kitchen,
        accentColor = PreservationGlow,
    ),
    LAND(
        key = "land",
        displayName = "Land",
        category = ModuleCategory.MANAGEMENT,
        enabledByDefault = false,
        displayOrder = 6,
        selectedIcon = Icons.Filled.Landscape,
        unselectedIcon = Icons.Outlined.Landscape,
        accentColor = LandGlow,
    ),
    FINANCES(
        key = "finances",
        displayName = "Finances",
        category = ModuleCategory.MANAGEMENT,
        enabledByDefault = false,
        displayOrder = 7,
        selectedIcon = Icons.Filled.Payments,
        unselectedIcon = Icons.Outlined.Payments,
        accentColor = FinanceGlow,
    ),
    COMPLIANCE(
        key = "compliance",
        displayName = "Compliance",
        category = ModuleCategory.MANAGEMENT,
        enabledByDefault = false,
        displayOrder = 8,
        selectedIcon = Icons.Filled.Balance,
        unselectedIcon = Icons.Outlined.Balance,
        accentColor = ComplianceGlow,
    ),
    ;

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String): FurrowModule? = byKey[key]
    }
}
