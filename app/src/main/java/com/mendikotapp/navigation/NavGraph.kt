package com.mendikotapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mendikotapp.ui.screens.*
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
                onStartGame = {
                    navController.navigate("game_mode_selection")
                }
            )
        }
        
        composable("game_mode_selection") {
            GameModeSelectionScreen(
                navController = navController,
                onModeSelected = { mode ->
                    viewModel.setGameMode(mode)
                }
            )
        }
        
        composable("player_setup/{mode}") { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            PlayerSetupScreen(
                onNavigateToGame = { 
                    navController.navigate("game") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                viewModel = viewModel,
                navController = navController
            )
        }
        
        composable("trump_selection") {
            TrumpSelectionScreen(
                viewModel = viewModel,
                onTrumpSelected = {
                    navController.navigate("game") {
                        popUpTo("player_setup") { inclusive = true }
                    }
                }
            )
        }
        
        composable("game") {
            GameScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
    }
} 