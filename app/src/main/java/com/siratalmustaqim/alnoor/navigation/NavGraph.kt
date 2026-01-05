package com.siratalmustaqim.alnoor.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.siratalmustaqim.alnoor.ui.screens.guard.GuardScreen
import com.siratalmustaqim.alnoor.ui.screens.home.HomeScreen
import com.siratalmustaqim.alnoor.ui.screens.settings.SettingsScreen

/**
 * Navigation graph for the bottom navigation inside MainScreen.
 * Contains only: Guard, Home, Settings
 */
@Composable
fun BottomNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(route = Screen.Guard.route) {
            GuardScreen()
        }
        composable(route = Screen.Home.route) {
            HomeScreen()
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
