package com.mendikotapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mendikotapp.ui.screens.HomeScreen
import com.mendikotapp.ui.screens.PlayerSetupScreen
import com.mendikotapp.ui.screens.SplashScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("home") {
            HomeScreen(navController)
        }
        composable("player_setup") {
            PlayerSetupScreen(navController)
        }
    }
} 