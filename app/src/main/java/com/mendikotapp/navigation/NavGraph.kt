package com.mendikotapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mendikotapp.ui.screens.GameScreen
import com.mendikotapp.ui.screens.HomeScreen
import com.mendikotapp.ui.screens.PlayerSetupScreen
import com.mendikotapp.viewmodel.GameSetupViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    // Create a shared ViewModel instance
    val viewModel: GameSetupViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToSetup = { 
                    navController.navigate("player_setup")
                }
            )
        }
        
        composable("player_setup") {
            PlayerSetupScreen(
                onNavigateToGame = { 
                    navController.navigate("game") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                viewModel = viewModel  // Pass the shared ViewModel
            )
        }
        
        composable("game") {
            GameScreen(viewModel = viewModel)  // Pass the same shared ViewModel
        }
    }
} 