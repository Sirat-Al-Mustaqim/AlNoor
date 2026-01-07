package com.siratalmustaqim.alnoor.navigation

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Home : Screen("home")
    data object Quran : Screen("quran")
    data object Duas : Screen("duas")
    data object Guard : Screen("guard")
    data object Settings : Screen("settings")
    
    // Settings sub-screens
    data object QuranSettings : Screen("settings/quran")
    data object PrayerSettings : Screen("settings/prayer")
    data object GuardSettings : Screen("settings/guard")
}
