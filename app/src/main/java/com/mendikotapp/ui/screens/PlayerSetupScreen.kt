package com.mendikotapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.mendikotapp.viewmodel.GameSetupViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSetupScreen(
    onNavigateToGame: () -> Unit,
    viewModel: GameSetupViewModel,
    navController: NavController
) {
    val gameMode by viewModel.gameMode.collectAsState()
    var player1Name by remember { mutableStateOf("") }
    var player2Name by remember { mutableStateOf("Player 2") }
    var player3Name by remember { mutableStateOf("Player 3") }
    var player4Name by remember { mutableStateOf("Player 4") }
    var showError by remember { mutableStateOf(false) }

    fun isNameValid(name: String) = name.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = if (gameMode == "single_player") 
                "Single Player Setup" 
            else 
                "4 Player Setup",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // Player 1 (Human player)
        OutlinedTextField(
            value = player1Name,
            onValueChange = { player1Name = it },
            label = { Text("Your Name") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null)
            },
            singleLine = true,
            isError = showError && player1Name.isBlank(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        if (gameMode == "manual_4_player") {
            // Player 2-4 inputs for manual mode
            OutlinedTextField(
                value = player2Name,
                onValueChange = { player2Name = it },
                label = { Text("Player 2 Name") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                singleLine = true,
                isError = showError && !isNameValid(player2Name),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = player3Name,
                onValueChange = { player3Name = it },
                label = { Text("Player 3 Name") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                singleLine = true,
                isError = showError && !isNameValid(player3Name),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = player4Name,
                onValueChange = { player4Name = it },
                label = { Text("Player 4 Name") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                singleLine = true,
                isError = showError && !isNameValid(player4Name),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            // Show bot names for single player mode
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "You'll be playing with:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text("🤖 Bot 1 (Team 2)")
                    Text("🤖 Bot 2 (Team 1)")
                    Text("🤖 Bot 3 (Team 2)")
                }
            }
        }

        // Error message
        AnimatedVisibility(
            visible = showError && (player1Name.isBlank() || 
                (gameMode == "manual_4_player" && 
                 (player2Name.isBlank() || player3Name.isBlank() || player4Name.isBlank()))),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = if (gameMode == "manual_4_player")
                    "Please enter names for all players"
                else
                    "Please enter your name",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Team information
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Team Setup",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Team 1: ${player1Name.ifBlank { "You" }} & ${player3Name}",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Team 2: $player2Name & $player4Name",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Start game button
        Button(
            onClick = {
                if (player1Name.isBlank() || 
                    (gameMode == "manual_4_player" && 
                     (player2Name.isBlank() || player3Name.isBlank() || player4Name.isBlank()))
                ) {
                    showError = true
                } else {
                    viewModel.initializeGame(
                        player1Name = player1Name,
                        player2Name = if (gameMode == "manual_4_player") player2Name else "Bot 1",
                        player3Name = if (gameMode == "manual_4_player") player3Name else "Bot 2",
                        player4Name = if (gameMode == "manual_4_player") player4Name else "Bot 3"
                    )
                    if (gameMode == "manual_4_player") {
                        navController.navigate("trump_selection")
                    } else {
                        onNavigateToGame()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Start Game")
        }
    }
} 