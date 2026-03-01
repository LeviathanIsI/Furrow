package com.furrow.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.furrow.app.data.FurrowModule
import com.furrow.app.ui.bees.BeeReportsScreen
import com.furrow.app.ui.bees.HiveDetailScreen
import com.furrow.app.ui.bees.HiveListScreen
import com.furrow.app.ui.bees.InspectionFormScreen
import com.furrow.app.ui.bees.TreatmentFormScreen
import com.furrow.app.ui.animals.AnimalDetailScreen
import com.furrow.app.ui.animals.AnimalFormScreen
import com.furrow.app.ui.animals.AnimalListScreen
import com.furrow.app.ui.animals.AnimalReportsScreen
import com.furrow.app.ui.animals.EggLogScreen
import com.furrow.app.ui.compliance.ComplianceFormScreen
import com.furrow.app.ui.compliance.ComplianceListScreen
import com.furrow.app.ui.finances.FinanceFormScreen
import com.furrow.app.ui.finances.FinanceListScreen
import com.furrow.app.ui.garden.BedDetailScreen
import com.furrow.app.ui.garden.GardenBedListScreen
import com.furrow.app.ui.garden.GardenReportsScreen
import com.furrow.app.ui.garden.HarvestLogScreen
import com.furrow.app.ui.garden.PlantDetailScreen
import com.furrow.app.ui.garden.PlantingCalendarScreen
import com.furrow.app.ui.garden.PlantingFormScreen
import com.furrow.app.ui.garden.SeasonPlannerScreen
import com.furrow.app.ui.home.HomeScreen
import com.furrow.app.ui.land.LandFormScreen
import com.furrow.app.ui.land.LandListScreen
import com.furrow.app.ui.orchard.OrchardFormScreen
import com.furrow.app.ui.orchard.OrchardListScreen
import com.furrow.app.ui.orchard.OrchardPlantDetailScreen
import com.furrow.app.ui.preservation.PantryScreen
import com.furrow.app.ui.preservation.PreservationFormScreen
import com.furrow.app.ui.preservation.PreservationListScreen
import com.furrow.app.ui.settings.SettingsScreen
import android.net.Uri

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    data object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    data object Bees : Screen("bees", "Bees", Icons.Filled.BugReport, Icons.Outlined.BugReport)
    data object Garden : Screen("garden", "Garden", Icons.Filled.Grass, Icons.Outlined.Grass)
    data object Animals : Screen("animals", "Animals", Icons.Filled.Pets, Icons.Outlined.Pets)
    data object Orchard : Screen("orchard", "Orchard", Icons.Filled.Forest, Icons.Outlined.Forest)
    data object Preservation : Screen("preservation", "Preservation", Icons.Filled.Kitchen, Icons.Outlined.Kitchen)
    data object Land : Screen("land", "Land", Icons.Filled.Landscape, Icons.Outlined.Landscape)
    data object Finances : Screen("finances", "Finances", Icons.Filled.Payments, Icons.Outlined.Payments)
    data object Compliance : Screen("compliance", "Compliance", Icons.Filled.Balance, Icons.Outlined.Balance)
    data object More : Screen("more", "More", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz)

    companion object {
        /** Maps FurrowModule.key to the corresponding Screen for navigation. */
        val allModuleScreens: Map<String, Screen> = mapOf(
            FurrowModule.BEES.key to Bees,
            FurrowModule.GARDEN.key to Garden,
            FurrowModule.ANIMALS.key to Animals,
            FurrowModule.ORCHARD.key to Orchard,
            FurrowModule.PRESERVATION.key to Preservation,
            FurrowModule.LAND.key to Land,
            FurrowModule.FINANCES.key to Finances,
            FurrowModule.COMPLIANCE.key to Compliance,
        )

        /** All routes that should show the bottom nav bar. */
        val bottomNavRoutes: Set<String> = setOf(
            Home.route, Bees.route, Garden.route,
            Animals.route, Orchard.route, Preservation.route,
            Land.route, Finances.route, Compliance.route, More.route,
        )
    }
}

/**
 * Builds the list of bottom nav screens based on enabled module keys (display-order).
 * Always starts with Home. If more than 4 modules enabled, shows first 3 + More.
 */
fun buildBottomNavScreens(enabledModuleKeys: List<String>): List<Screen> {
    val moduleScreens = enabledModuleKeys.mapNotNull { Screen.allModuleScreens[it] }
    return if (moduleScreens.size <= 4) {
        listOf(Screen.Home) + moduleScreens
    } else {
        listOf(Screen.Home) + moduleScreens.take(3) + Screen.More
    }
}

@Composable
fun FurrowNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    enabledModules: Set<String> = emptySet(),
    deepLinkAction: String? = null,
) {
    val navigateToBottomTab: (Screen) -> Unit = { screen ->
        navController.navigate(screen.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
        enterTransition = { fadeIn(tween(200)) },
        exitTransition = { fadeOut(tween(200)) },
        popEnterTransition = { fadeIn(tween(200)) },
        popExitTransition = { fadeOut(tween(200)) },
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onSettingsClick = { navController.navigate("settings") },
                onNavigateToBees = { navigateToBottomTab(Screen.Bees) },
                onNavigateToGarden = { navigateToBottomTab(Screen.Garden) },
                onNavigateToEggLog = { navController.navigate("animals/egg-log") },
                onNavigateToInspection = { navigateToBottomTab(Screen.Bees) },
                onNavigateToHarvest = { navigateToBottomTab(Screen.Garden) },
                onNavigateToAnimals = { navigateToBottomTab(Screen.Animals) },
                onNavigateToOrchard = { navigateToBottomTab(Screen.Orchard) },
                onNavigateToPreservation = { navigateToBottomTab(Screen.Preservation) },
                onNavigateToLand = { navigateToBottomTab(Screen.Land) },
                onNavigateToFinances = { navigateToBottomTab(Screen.Finances) },
                onNavigateToCompliance = { navigateToBottomTab(Screen.Compliance) },
                onNavigateToRoute = { route -> navController.navigate(route) },
                enabledModules = enabledModules,
                deepLinkAction = deepLinkAction,
            )
        }

        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() },
            )
        }

        // ── Bees ──

        composable(Screen.Bees.route) {
            HiveListScreen(
                onHiveClick = { hiveId -> navController.navigate("bees/$hiveId") },
                onReportsClick = { navController.navigate("bees/reports") },
            )
        }

        composable("bees/reports") {
            BeeReportsScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = "bees/{hiveId}",
            arguments = listOf(navArgument("hiveId") { type = NavType.LongType })
        ) {
            HiveDetailScreen(
                onBack = { navController.popBackStack() },
                onAddInspection = { hiveId -> navController.navigate("bees/$hiveId/add-inspection") },
                onAddTreatment = { hiveId -> navController.navigate("bees/$hiveId/add-treatment") },
                onEditInspection = { hiveId, editId -> navController.navigate("bees/$hiveId/add-inspection?editId=$editId") },
                onEditTreatment = { hiveId, editId -> navController.navigate("bees/$hiveId/add-treatment?editId=$editId") },
            )
        }

        composable(
            route = "bees/{hiveId}/add-inspection?editId={editId}",
            arguments = listOf(
                navArgument("hiveId") { type = NavType.LongType },
                navArgument("editId") { type = NavType.LongType; defaultValue = 0L },
            )
        ) { backStackEntry ->
            val hiveId = backStackEntry.arguments?.getLong("hiveId") ?: return@composable
            val editId = backStackEntry.arguments?.getLong("editId") ?: 0L
            InspectionFormScreen(
                hiveId = hiveId,
                editId = editId,
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = "bees/{hiveId}/add-treatment?editId={editId}",
            arguments = listOf(
                navArgument("hiveId") { type = NavType.LongType },
                navArgument("editId") { type = NavType.LongType; defaultValue = 0L },
            )
        ) { backStackEntry ->
            val hiveId = backStackEntry.arguments?.getLong("hiveId") ?: return@composable
            val editId = backStackEntry.arguments?.getLong("editId") ?: 0L
            TreatmentFormScreen(
                hiveId = hiveId,
                editId = editId,
                onBack = { navController.popBackStack() },
            )
        }

        // ── Garden ──

        composable(Screen.Garden.route) {
            GardenBedListScreen(
                onBedClick = { bedId -> navController.navigate("garden/$bedId") },
                onReportsClick = { navController.navigate("garden/reports") },
                onCalendarClick = { navController.navigate("garden/calendar") },
                onSeasonPlannerClick = { navController.navigate("garden/season-planner") },
            )
        }

        composable("garden/season-planner") {
            SeasonPlannerScreen(
                onBack = { navController.popBackStack() },
                onFinished = { navController.popBackStack() },
            )
        }

        composable("garden/calendar") {
            PlantingCalendarScreen(
                onBack = { navController.popBackStack() },
                onBedClick = { bedId -> navController.navigate("garden/$bedId") },
            )
        }

        composable("garden/reports") {
            GardenReportsScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = "garden/{bedId}",
            arguments = listOf(navArgument("bedId") { type = NavType.LongType })
        ) {
            BedDetailScreen(
                onBack = { navController.popBackStack() },
                onAddPlanting = { bedId -> navController.navigate("garden/$bedId/add-planting") },
                onAddHarvest = { bedId -> navController.navigate("garden/$bedId/add-harvest") },
                onEditPlanting = { bedId, editId -> navController.navigate("garden/$bedId/add-planting?editId=$editId") },
                onEditHarvest = { bedId, editId -> navController.navigate("garden/$bedId/add-harvest?editId=$editId") },
                onViewPlanting = { bedId, plantingId -> navController.navigate("garden/$bedId/planting/$plantingId") },
            )
        }

        composable(
            route = "garden/{bedId}/add-planting?editId={editId}",
            arguments = listOf(
                navArgument("bedId") { type = NavType.LongType },
                navArgument("editId") { type = NavType.LongType; defaultValue = 0L },
            )
        ) { backStackEntry ->
            val bedId = backStackEntry.arguments?.getLong("bedId") ?: return@composable
            val editId = backStackEntry.arguments?.getLong("editId") ?: 0L
            PlantingFormScreen(
                bedId = bedId,
                editId = editId,
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = "garden/{bedId}/add-harvest?editId={editId}",
            arguments = listOf(
                navArgument("bedId") { type = NavType.LongType },
                navArgument("editId") { type = NavType.LongType; defaultValue = 0L },
            )
        ) { backStackEntry ->
            val bedId = backStackEntry.arguments?.getLong("bedId") ?: return@composable
            val editId = backStackEntry.arguments?.getLong("editId") ?: 0L
            HarvestLogScreen(
                bedId = bedId,
                editId = editId,
                onBack = { navController.popBackStack() },
                onNavigateToPreservation = if ("preservation" in enabledModules) {
                    { type, itemName ->
                        navController.popBackStack()
                        navController.navigate("preservation/add?type=$type&editId=0&itemName=${Uri.encode(itemName)}")
                    }
                } else null,
            )
        }

        composable(
            route = "garden/{bedId}/planting/{plantingId}",
            arguments = listOf(
                navArgument("bedId") { type = NavType.LongType },
                navArgument("plantingId") { type = NavType.LongType },
            )
        ) {
            PlantDetailScreen(
                onBack = { navController.popBackStack() },
            )
        }

        // ── Animals ──

        composable(Screen.Animals.route) {
            AnimalListScreen(
                onAnimalClick = { animalId -> navController.navigate("animals/$animalId") },
                onAddAnimal = { navController.navigate("animals/add?type=animal") },
                onAddEgg = { navController.navigate("animals/egg-log") },
                onEditEgg = { editId -> navController.navigate("animals/egg-log?editId=$editId") },
                onReportsClick = { navController.navigate("animals/reports") },
            )
        }

        composable(
            route = "animals/{animalId}",
            arguments = listOf(navArgument("animalId") { type = NavType.LongType }),
        ) {
            AnimalDetailScreen(
                onBack = { navController.popBackStack() },
                onAddLog = { animalId, logType -> navController.navigate("animals/$animalId/add?type=$logType") },
                onEditLog = { animalId, logType, editId -> navController.navigate("animals/$animalId/add?type=$logType&editId=$editId") },
            )
        }

        composable(
            route = "animals/add?type={type}&editId={editId}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType; defaultValue = "animal" },
                navArgument("editId") { type = NavType.LongType; defaultValue = 0L },
            ),
        ) { backStackEntry ->
            AnimalFormScreen(
                type = backStackEntry.arguments?.getString("type") ?: "animal",
                editId = backStackEntry.arguments?.getLong("editId") ?: 0L,
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = "animals/{animalId}/add?type={type}&editId={editId}",
            arguments = listOf(
                navArgument("animalId") { type = NavType.LongType },
                navArgument("type") { type = NavType.StringType; defaultValue = "health" },
                navArgument("editId") { type = NavType.LongType; defaultValue = 0L },
            ),
        ) { backStackEntry ->
            AnimalFormScreen(
                type = backStackEntry.arguments?.getString("type") ?: "health",
                animalId = backStackEntry.arguments?.getLong("animalId") ?: 0L,
                editId = backStackEntry.arguments?.getLong("editId") ?: 0L,
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = "animals/egg-log?editId={editId}",
            arguments = listOf(
                navArgument("editId") { type = NavType.LongType; defaultValue = 0L },
            ),
        ) { backStackEntry ->
            val editId = backStackEntry.arguments?.getLong("editId") ?: 0L
            EggLogScreen(
                editId = editId,
                onBack = { navController.popBackStack() },
            )
        }

        composable("animals/reports") {
            AnimalReportsScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        // ── Orchard ──

        composable(Screen.Orchard.route) {
            OrchardListScreen(
                onPlantClick = { plantId -> navController.navigate("orchard/$plantId") },
                onAddPlant = { navController.navigate("orchard/add?type=plant") },
            )
        }

        composable(
            route = "orchard/{plantId}",
            arguments = listOf(navArgument("plantId") { type = NavType.LongType }),
        ) {
            OrchardPlantDetailScreen(
                onBack = { navController.popBackStack() },
                onAddLog = { plantId, logType -> navController.navigate("orchard/$plantId/add?type=$logType") },
                onEditLog = { plantId, logType, editId -> navController.navigate("orchard/$plantId/add?type=$logType&editId=$editId") },
            )
        }

        composable(
            route = "orchard/add?type={type}&editId={editId}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType; defaultValue = "plant" },
                navArgument("editId") { type = NavType.LongType; defaultValue = 0L },
            ),
        ) { backStackEntry ->
            OrchardFormScreen(
                type = backStackEntry.arguments?.getString("type") ?: "plant",
                editId = backStackEntry.arguments?.getLong("editId") ?: 0L,
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = "orchard/{plantId}/add?type={type}&editId={editId}",
            arguments = listOf(
                navArgument("plantId") { type = NavType.LongType },
                navArgument("type") { type = NavType.StringType; defaultValue = "harvest" },
                navArgument("editId") { type = NavType.LongType; defaultValue = 0L },
            ),
        ) { backStackEntry ->
            OrchardFormScreen(
                type = backStackEntry.arguments?.getString("type") ?: "harvest",
                plantId = backStackEntry.arguments?.getLong("plantId") ?: 0L,
                editId = backStackEntry.arguments?.getLong("editId") ?: 0L,
                onBack = { navController.popBackStack() },
            )
        }

        // ── Preservation ──

        composable(Screen.Preservation.route) {
            PreservationListScreen(
                onNavigateToPantry = { navController.navigate("preservation/pantry") },
                onAddBatch = { batchType -> navController.navigate("preservation/add?type=$batchType") },
                onEditBatch = { batchType, editId -> navController.navigate("preservation/add?type=$batchType&editId=$editId") },
            )
        }

        composable("preservation/pantry") {
            PantryScreen(
                onBack = { navController.popBackStack() },
                onAddPantryItem = { navController.navigate("preservation/add?type=pantry") },
                onEditPantryItem = { editId -> navController.navigate("preservation/add?type=pantry&editId=$editId") },
            )
        }

        composable(
            route = "preservation/add?type={type}&editId={editId}&itemName={itemName}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType; defaultValue = "canning" },
                navArgument("editId") { type = NavType.LongType; defaultValue = 0L },
                navArgument("itemName") { type = NavType.StringType; defaultValue = "" },
            ),
        ) { backStackEntry ->
            PreservationFormScreen(
                type = backStackEntry.arguments?.getString("type") ?: "canning",
                editId = backStackEntry.arguments?.getLong("editId") ?: 0L,
                initialItemName = backStackEntry.arguments?.getString("itemName") ?: "",
                onBack = { navController.popBackStack() },
            )
        }

        // ── Land ──

        composable(Screen.Land.route) {
            LandListScreen(
                onAddItem = { itemType -> navController.navigate("land/add?type=$itemType") },
                onEditItem = { itemType, editId -> navController.navigate("land/add?type=$itemType&editId=$editId") },
            )
        }

        composable(
            route = "land/add?type={type}&editId={editId}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType; defaultValue = "property" },
                navArgument("editId") { type = NavType.LongType; defaultValue = 0L },
            ),
        ) { backStackEntry ->
            LandFormScreen(
                type = backStackEntry.arguments?.getString("type") ?: "property",
                editId = backStackEntry.arguments?.getLong("editId") ?: 0L,
                onBack = { navController.popBackStack() },
            )
        }

        // ── Finances ──

        composable(Screen.Finances.route) {
            FinanceListScreen(
                onAddItem = { itemType -> navController.navigate("finances/add?type=$itemType") },
                onEditItem = { itemType, editId -> navController.navigate("finances/add?type=$itemType&editId=$editId") },
            )
        }

        composable(
            route = "finances/add?type={type}&editId={editId}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType; defaultValue = "expense" },
                navArgument("editId") { type = NavType.LongType; defaultValue = 0L },
            ),
        ) { backStackEntry ->
            FinanceFormScreen(
                type = backStackEntry.arguments?.getString("type") ?: "expense",
                editId = backStackEntry.arguments?.getLong("editId") ?: 0L,
                onBack = { navController.popBackStack() },
            )
        }

        // ── Compliance ──

        composable(Screen.Compliance.route) {
            ComplianceListScreen(
                onAddItem = { itemType -> navController.navigate("compliance/add?type=$itemType") },
                onEditItem = { itemType, editId -> navController.navigate("compliance/add?type=$itemType&editId=$editId") },
            )
        }

        composable(
            route = "compliance/add?type={type}&editId={editId}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType; defaultValue = "permit" },
                navArgument("editId") { type = NavType.LongType; defaultValue = 0L },
            ),
        ) { backStackEntry ->
            ComplianceFormScreen(
                type = backStackEntry.arguments?.getString("type") ?: "permit",
                editId = backStackEntry.arguments?.getLong("editId") ?: 0L,
                onBack = { navController.popBackStack() },
            )
        }

        // ── More (overflow hub) ──

        composable(Screen.More.route) {
            MoreScreen(
                enabledModules = enabledModules,
                onModuleClick = { moduleKey ->
                    Screen.allModuleScreens[moduleKey]?.let { screen ->
                        navigateToBottomTab(screen)
                    }
                },
            )
        }
    }
}
