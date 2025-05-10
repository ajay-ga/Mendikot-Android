package com.mendikotapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class GameMode(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val emoji: String
)

@Composable
fun GameModeSelectionScreen(
    navController: NavController,
    onModeSelected: (String) -> Unit
) {
    val gameModes = listOf(
        GameMode(
            id = "single_player",
            title = "Single Player",
            description = "Play against 3 AI opponents",
            icon = Icons.Filled.Person,
            emoji = "🧍"
        ),
        GameMode(
            id = "manual_4_player",
            title = "Manual 4 Player",
            description = "Local multiplayer with friends",
            icon = Icons.Filled.Groups,
            emoji = "🤝"
        )
    )

    var selectedMode by remember { mutableStateOf<String?>(null) }
    var showConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "Select Game Mode",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Game mode cards
        gameModes.forEach { mode ->
            GameModeCard(
                gameMode = mode,
                selected = selectedMode == mode.id,
                onSelect = {
                    selectedMode = mode.id
                    showConfirmation = true
                }
            )
        }

        // Confirmation button
        AnimatedVisibility(
            visible = showConfirmation,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Button(
                onClick = {
                    selectedMode?.let { mode ->
                        onModeSelected(mode)
                        navController.navigate("player_setup/$mode")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Continue")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameModeCard(
    gameMode: GameMode,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .selectable(
                selected = selected,
                onClick = onSelect
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 8.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mode icon and emoji
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = gameMode.icon,
                    contentDescription = null,
                    tint = if (selected) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = gameMode.emoji,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 24.dp)
                )
            }

            // Mode details
            Column {
                Text(
                    text = gameMode.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (selected) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = gameMode.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selected) 
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
} 