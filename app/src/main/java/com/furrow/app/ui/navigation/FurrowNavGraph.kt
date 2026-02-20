package com.furrow.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.furrow.app.ui.bees.BeeReportsScreen
import com.furrow.app.ui.bees.HiveDetailScreen
import com.furrow.app.ui.bees.HiveListScreen
import com.furrow.app.ui.bees.InspectionFormScreen
import com.furrow.app.ui.bees.TreatmentFormScreen
import com.furrow.app.ui.garden.BedDetailScreen
import com.furrow.app.ui.garden.GardenBedListScreen
import com.furrow.app.ui.garden.GardenReportsScreen
import com.furrow.app.ui.garden.HarvestLogScreen
import com.furrow.app.ui.garden.PlantingFormScreen
import com.furrow.app.ui.home.HomeScreen
import com.furrow.app.ui.poultry.ChickenDetailScreen
import com.furrow.app.ui.poultry.EggLogScreen
import com.furrow.app.ui.poultry.FlockScreen
import com.furrow.app.ui.poultry.PoultryReportsScreen
import com.furrow.app.ui.settings.SettingsScreen

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    data object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    data object Bees : Screen("bees", "Bees", Icons.Filled.BugReport, Icons.Outlined.BugReport)
    data object Poultry : Screen("poultry", "Poultry", Icons.Filled.Egg, Icons.Outlined.Egg)
    data object Garden : Screen("garden", "Garden", Icons.Filled.Grass, Icons.Outlined.Grass)
}

val bottomNavScreens = listOf(Screen.Home, Screen.Bees, Screen.Poultry, Screen.Garden)

@Composable
fun FurrowNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
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
        enterTransition = {
            fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 }
        },
        exitTransition = {
            fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { -it / 4 }
        },
        popEnterTransition = {
            fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -it / 4 }
        },
        popExitTransition = {
            fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { it / 4 }
        },
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onSettingsClick = { navController.navigate("settings") },
                onNavigateToBees = { navigateToBottomTab(Screen.Bees) },
                onNavigateToPoultry = { navigateToBottomTab(Screen.Poultry) },
                onNavigateToGarden = { navigateToBottomTab(Screen.Garden) },
                onNavigateToEggLog = { navController.navigate("poultry/add-egg") },
                onNavigateToInspection = { navigateToBottomTab(Screen.Bees) },
                onNavigateToHarvest = { navigateToBottomTab(Screen.Garden) },
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

        // ── Poultry ──

        composable(Screen.Poultry.route) {
            FlockScreen(
                onAddEgg = { navController.navigate("poultry/add-egg") },
                onEditEgg = { editId -> navController.navigate("poultry/add-egg?editId=$editId") },
                onChickenClick = { chickenId -> navController.navigate("poultry/$chickenId") },
                onReportsClick = { navController.navigate("poultry/reports") },
            )
        }

        composable("poultry/reports") {
            PoultryReportsScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route = "poultry/add-egg?editId={editId}",
            arguments = listOf(
                navArgument("editId") { type = NavType.LongType; defaultValue = 0L },
            )
        ) { backStackEntry ->
            val editId = backStackEntry.arguments?.getLong("editId") ?: 0L
            EggLogScreen(
                editId = editId,
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = "poultry/{chickenId}",
            arguments = listOf(navArgument("chickenId") { type = NavType.LongType })
        ) {
            ChickenDetailScreen(
                onBack = { navController.popBackStack() },
            )
        }

        // ── Garden ──

        composable(Screen.Garden.route) {
            GardenBedListScreen(
                onBedClick = { bedId -> navController.navigate("garden/$bedId") },
                onReportsClick = { navController.navigate("garden/reports") },
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
            )
        }
    }
}
