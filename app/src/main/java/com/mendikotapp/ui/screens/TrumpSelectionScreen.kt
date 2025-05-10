package com.mendikotapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mendikotapp.ui.components.PlayingCard
import com.mendikotapp.viewmodel.GameSetupViewModel

@Composable
fun TrumpSelectionScreen(
    viewModel: GameSetupViewModel,
    onTrumpSelected: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    var showPassDeviceDialog by remember { mutableStateOf(true) }
    var selectedCardIndex by remember { mutableStateOf<Int?>(null) }

    val rightOfDealer = ((gameState?.currentDealer ?: 0) + 1) % 4
    val currentPlayer = gameState?.players?.get(rightOfDealer)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title and instructions
        Text(
            text = "Trump Selection",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // Pass device dialog
        if (showPassDeviceDialog) {
            AlertDialog(
                onDismissRequest = { showPassDeviceDialog = false },
                title = { Text("Pass the Device") },
                text = { 
                    Text(
                        "Please pass the device to ${currentPlayer?.name} " +
                        "to select the trump card"
                    ) 
                },
                confirmButton = {
                    Button(onClick = { showPassDeviceDialog = false }) {
                        Text("Continue")
                    }
                }
            )
        }

        // Player info
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${currentPlayer?.name}'s Turn",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Select one card as trump",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }

        // Player's hand
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)  // Fixed height instead of weight
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy((-60).dp),  // Increase overlap
                contentPadding = PaddingValues(horizontal = 64.dp)  // Increase padding to center better
            ) {
                items(currentPlayer?.hand?.size ?: 0) { index ->
                    val card = currentPlayer?.hand?.get(index)
                    if (card != null) {
                        PlayingCard(
                            card = card,
                            onClick = { selectedCardIndex = index },
                            isSelected = selectedCardIndex == index,
                            faceDown = false,
                            modifier = Modifier
                                .size(100.dp, 140.dp)  // Fixed size for cards
                                .padding(4.dp)
                                .graphicsLayer {
                                    // Lift and scale selected card
                                    translationY = if (selectedCardIndex == index) -16f else 0f
                                    scaleX = if (selectedCardIndex == index) 1.1f else 1f
                                    scaleY = if (selectedCardIndex == index) 1.1f else 1f
                                }
                        )
                    }
                }
            }
        }

        // Confirm button
        AnimatedVisibility(
            visible = selectedCardIndex != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Button(
                onClick = {
                    selectedCardIndex?.let { index ->
                        viewModel.selectTrump(index)
                        onTrumpSelected()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Confirm Trump Selection")
            }
        }
    }
} 