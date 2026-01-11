package com.siratalmustaqim.alnoor.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.siratalmustaqim.alnoor.navigation.BottomNavGraph
import com.siratalmustaqim.alnoor.ui.components.BottomNavigationBar

@Composable
fun MainScreen(
    rootNavController: NavHostController
) {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        BottomNavGraph(
            navController = navController,
            rootNavController = rootNavController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    com.siratalmustaqim.alnoor.ui.theme.AlNoorTheme {
        MainScreen(rootNavController = rememberNavController())
    }
}

