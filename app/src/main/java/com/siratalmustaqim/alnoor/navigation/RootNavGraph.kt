package com.siratalmustaqim.alnoor.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.siratalmustaqim.alnoor.ui.screens.MainScreen
import com.siratalmustaqim.alnoor.ui.screens.duas.DuasScreen
import com.siratalmustaqim.alnoor.ui.screens.quran.QuranScreen
import com.siratalmustaqim.alnoor.ui.screens.splash.SplashScreen
import com.siratalmustaqim.alnoor.ui.screens.verification.AyahVerificationScreen

/**
 * Root navigation graph for the entire app.
 * - Splash: Initial splash screen
 * - Main: Contains bottom navigation with Guard, Home, Settings
 * - Quran: Standalone screen without bottom nav
 * - Duas: Standalone screen without bottom nav
 * - AyahVerification: Verification screen for disabling always-on protection
 */
@Composable
fun RootNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.Main.route) {
            MainScreen(rootNavController = navController)
        }
        composable(route = Screen.Quran.route) {
            QuranScreen()
        }
        composable(route = Screen.Duas.route) {
            DuasScreen()
        }
        composable(route = Screen.AyahVerification.route) {
            AyahVerificationScreen(
                onVerificationSuccess = {
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
    }
}
