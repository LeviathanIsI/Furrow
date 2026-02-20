package com.furrow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.furrow.app.ui.MainViewModel
import com.furrow.app.ui.components.AppBottomNav
import com.furrow.app.ui.components.AppNavItem
import com.furrow.app.ui.navigation.FurrowNavGraph
import com.furrow.app.ui.navigation.bottomNavScreens
import com.furrow.app.ui.onboarding.OnboardingScreen
import com.furrow.app.ui.splash.SplashScreen
import com.furrow.app.ui.theme.FurrowTheme
import com.furrow.app.util.NotificationPermissionScreen
import com.furrow.app.util.RequestNotificationPermission
import com.furrow.app.util.shouldAskNotificationPermission
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FurrowTheme {
                FurrowApp()
            }
        }
    }
}

@Composable
fun FurrowApp() {
    var splashFinished by remember { mutableStateOf(false) }

    if (!splashFinished) {
        SplashScreen(onFinished = { splashFinished = true })
    } else {
        val mainViewModel: MainViewModel = hiltViewModel()
        val hasProfile by mainViewModel.hasProfile.collectAsState()
        val notificationPromptShown by mainViewModel.notificationPromptShown.collectAsState()

        when (hasProfile) {
            null -> {
                // Brief loading while Room emits.
            }
            false -> {
                OnboardingScreen()
            }
            true -> {
                val context = LocalContext.current
                val shouldShowPrompt = !notificationPromptShown &&
                    shouldAskNotificationPermission(context)

                if (shouldShowPrompt) {
                    var requestPermission by remember { mutableStateOf(false) }

                    if (requestPermission) {
                        RequestNotificationPermission {
                            mainViewModel.onNotificationPromptDone()
                        }
                    }

                    NotificationPermissionScreen(
                        onEnableClick = { requestPermission = true },
                        onSkipClick = { mainViewModel.onNotificationPromptDone() },
                    )
                } else {
                    MainContent()
                }
            }
        }
    }
}

@Composable
private fun MainContent() {
    val navController = rememberNavController()

    androidx.compose.material3.Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val showBottomNav = currentRoute in bottomNavScreens.map { it.route }
            if (showBottomNav) {
                val items = bottomNavScreens.map {
                    AppNavItem(
                        route = it.route,
                        label = it.title,
                        selectedIcon = it.selectedIcon,
                        unselectedIcon = it.unselectedIcon,
                    )
                }
                AppBottomNav(
                    items = items,
                    currentRoute = currentRoute,
                    onItemSelected = { item ->
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        FurrowNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
