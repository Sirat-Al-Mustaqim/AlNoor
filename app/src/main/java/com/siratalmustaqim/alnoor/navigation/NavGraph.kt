package com.siratalmustaqim.alnoor.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.siratalmustaqim.alnoor.ui.screens.duas.DuasScreen
import com.siratalmustaqim.alnoor.ui.screens.guard.GuardScreen
import com.siratalmustaqim.alnoor.ui.screens.home.HomeScreen
import com.siratalmustaqim.alnoor.ui.screens.more.MoreScreen
import com.siratalmustaqim.alnoor.ui.screens.qibla.QiblaScreen
import com.siratalmustaqim.alnoor.ui.screens.quran.QuranScreen
import com.siratalmustaqim.alnoor.ui.screens.settings.SettingsScreen
import com.siratalmustaqim.alnoor.ui.screens.settings.prayer.PrayerSettingsScreen
import com.siratalmustaqim.alnoor.ui.screens.settings.quran.QuranSettingsScreen

/**
 * Navigation graph for the bottom navigation inside MainScreen.
 * Contains: Home, Qibla, Guard, More (+ sub-screens)
 */
@Composable
fun BottomNavGraph(
    navController: NavHostController,
    rootNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Bottom Nav Destinations
        composable(route = Screen.Home.route) {
            HomeScreen()
        }
        composable(route = Screen.Qibla.route) {
            QiblaScreen()
        }
        composable(route = Screen.Guard.route) {
            GuardScreen(
                onNavigateToVerification = {
                    rootNavController.navigate(Screen.AyahVerification.route)
                }
            )
        }
        composable(route = Screen.More.route) {
            MoreScreen(
                onQuranClick = { navController.navigate(Screen.Quran.route) },
                onDuasClick = { navController.navigate(Screen.Duas.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }

        // Screens from More menu
        composable(route = Screen.Quran.route) {
            QuranScreen()
        }
        composable(route = Screen.Duas.route) {
            DuasScreen()
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onQuranSettingsClick = { navController.navigate(Screen.QuranSettings.route) },
                onPrayerSettingsClick = { navController.navigate(Screen.PrayerSettings.route) },
            )
        }

        // Settings sub-screens
        composable(route = Screen.QuranSettings.route) {
            QuranSettingsScreen(onBackClick = { navController.popBackStack() })
        }
        composable(route = Screen.PrayerSettings.route) {
            PrayerSettingsScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
