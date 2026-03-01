package com.furrow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.furrow.app.ui.splash.SplashScreen
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.furrow.app.ui.MainViewModel
import com.furrow.app.ui.components.AppBottomNav
import com.furrow.app.ui.components.AppNavItem
import com.furrow.app.ui.navigation.FurrowNavGraph
import com.furrow.app.ui.navigation.Screen
import com.furrow.app.ui.navigation.buildBottomNavScreens
import com.furrow.app.ui.onboarding.OnboardingScreen
import com.furrow.app.ui.theme.FurrowTheme
import com.furrow.app.util.NotificationPermissionScreen
import com.furrow.app.util.RequestNotificationPermission
import com.furrow.app.util.shouldAskNotificationPermission
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val deepLinkAction = intent?.data?.host
        setContent {
            FurrowTheme {
                FurrowApp(mainViewModel = mainViewModel, deepLinkAction = deepLinkAction)
            }
        }
    }
}

@Composable
fun FurrowApp(mainViewModel: MainViewModel, deepLinkAction: String? = null) {
    var splashFinished by remember { mutableStateOf(false) }

    if (!splashFinished) {
        SplashScreen(onFinished = { splashFinished = true })
    } else {
        val hasProfile by mainViewModel.hasProfile.collectAsState()
        val notificationPromptShown by mainViewModel.notificationPromptShown.collectAsState()

        when (hasProfile) {
            null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
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
                    MainContent(mainViewModel = mainViewModel, deepLinkAction = deepLinkAction)
                }
            }
        }
    }
}

@Composable
private fun MainContent(mainViewModel: MainViewModel, deepLinkAction: String? = null) {
    val navController = rememberNavController()
    val enabledModulesOrdered by mainViewModel.enabledModulesOrdered.collectAsState()
    val enabledModuleKeys by mainViewModel.enabledModuleKeys.collectAsState()

    val bottomNavScreens = remember(enabledModulesOrdered) {
        buildBottomNavScreens(enabledModulesOrdered)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(modifier = Modifier.fillMaxSize()) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding),
        ) {
            // NavHost fills the full screen — bottom padding never changes
            FurrowNavGraph(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                enabledModules = enabledModuleKeys,
                deepLinkAction = deepLinkAction,
            )

            // Bottom nav is an overlay, not a Scaffold layout element
            val showBottomNav = currentRoute in Screen.bottomNavRoutes

            AnimatedVisibility(
                visible = showBottomNav,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = fadeIn(animationSpec = tween(150)),
                exit = fadeOut(animationSpec = tween(150)),
            ) {
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
        }
    }
}
