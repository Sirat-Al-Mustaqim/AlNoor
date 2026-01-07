package com.siratalmustaqim.alnoor.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.siratalmustaqim.alnoor.ui.screens.guard.GuardScreen
import com.siratalmustaqim.alnoor.ui.screens.home.HomeScreen
import com.siratalmustaqim.alnoor.ui.screens.settings.SettingsScreen
import com.siratalmustaqim.alnoor.ui.screens.settings.guard.GuardSettingsScreen
import com.siratalmustaqim.alnoor.ui.screens.settings.prayer.PrayerSettingsScreen
import com.siratalmustaqim.alnoor.ui.screens.settings.quran.QuranSettingsScreen

/**
 * Navigation graph for the bottom navigation inside MainScreen.
 * Contains: Guard, Home, Settings (+ sub-screens)
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
            SettingsScreen(
                onQuranSettingsClick = { navController.navigate(Screen.QuranSettings.route) },
                onPrayerSettingsClick = { navController.navigate(Screen.PrayerSettings.route) },
                onGuardSettingsClick = { navController.navigate(Screen.GuardSettings.route) }
            )
        }
        
        // Settings sub-screens
        composable(route = Screen.QuranSettings.route) {
            QuranSettingsScreen(onBackClick = { navController.popBackStack() })
        }
        composable(route = Screen.PrayerSettings.route) {
            PrayerSettingsScreen(onBackClick = { navController.popBackStack() })
        }
        composable(route = Screen.GuardSettings.route) {
            GuardSettingsScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
