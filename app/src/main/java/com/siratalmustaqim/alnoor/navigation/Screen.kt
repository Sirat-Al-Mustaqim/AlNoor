package com.siratalmustaqim.alnoor.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Quran : Screen("quran")
    data object Duas : Screen("duas")
    data object Guard : Screen("guard")
    data object Settings : Screen("settings")
}
