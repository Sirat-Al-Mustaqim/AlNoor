package com.siratalmustaqim.alnoor.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.siratalmustaqim.alnoor.ui.screens.duas.DuasScreen
import com.siratalmustaqim.alnoor.ui.screens.guard.GuardScreen
import com.siratalmustaqim.alnoor.ui.screens.home.HomeScreen
import com.siratalmustaqim.alnoor.ui.screens.quran.QuranScreen
import com.siratalmustaqim.alnoor.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen()
        }
        composable(route = Screen.Quran.route) {
            QuranScreen()
        }
        composable(route = Screen.Duas.route) {
            DuasScreen()
        }
        composable(route = Screen.Guard.route) {
            GuardScreen()
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
