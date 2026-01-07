package com.siratalmustaqim.alnoor.navigation

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Home : Screen("home")
    data object Qibla : Screen("qibla")
    data object Guard : Screen("guard")
    data object More : Screen("more")
    
    // Screens accessible from More
    data object Quran : Screen("quran")
    data object Duas : Screen("duas")
    data object Settings : Screen("settings")
    
    // Settings sub-screens
    data object QuranSettings : Screen("settings/quran")
    data object PrayerSettings : Screen("settings/prayer")
    data object GuardSettings : Screen("settings/guard")
}
