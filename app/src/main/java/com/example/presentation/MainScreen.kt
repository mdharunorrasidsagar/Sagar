package com.example.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.presentation.screens.DashboardScreen
import com.example.presentation.screens.HistoryScreen
import com.example.presentation.screens.OnboardingScreen
import com.example.presentation.screens.ProfileScreen

sealed class Screen(val route: String, val title: String) {
    object Onboarding : Screen("onboarding", "Onboarding")
    object Dashboard : Screen("dashboard", "Dashboard")
    object History : Screen("history", "Analytics")
    object Profile : Screen("profile", "Profile")
}

@Composable
fun MainScreen(viewModel: HealthViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check onboarding completion and navigate appropriately
    LaunchedEffect(userProfile) {
        val profile = userProfile
        if (profile != null) {
            if (!profile.isOnboardingCompleted && currentRoute != Screen.Onboarding.route) {
                navController.navigate(Screen.Onboarding.route) {
                    popUpTo(0) { inclusive = true }
                }
            } else if (profile.isOnboardingCompleted && currentRoute == Screen.Onboarding.route) {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        } else {
            // First time loading - default profile is null, force onboarding
            if (currentRoute != Screen.Onboarding.route) {
                navController.navigate(Screen.Onboarding.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    val showBottomBar = currentRoute in listOf(Screen.Dashboard.route, Screen.History.route, Screen.Profile.route)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = NavigationBarDefaults.Elevation
                ) {
                    val items = listOf(
                        Triple(Screen.Dashboard, Icons.Filled.Home, Icons.Outlined.Home),
                        Triple(Screen.History, Icons.Filled.History, Icons.Outlined.History),
                        Triple(Screen.Profile, Icons.Filled.Person, Icons.Outlined.Person)
                    )

                    items.forEach { (screen, selectedIcon, unselectedIcon) ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            modifier = Modifier.testTag("nav_item_${screen.route}"),
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) selectedIcon else unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                unselectedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            ),
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(viewModel = viewModel)
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(viewModel = viewModel, onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                })
            }
            composable(Screen.History.route) {
                HistoryScreen(viewModel = viewModel)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(viewModel = viewModel)
            }
        }
    }
}
