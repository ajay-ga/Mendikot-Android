package com.mendikotapp.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mendikotapp.data.models.RoundResult
import com.mendikotapp.data.models.WinType

@Composable
fun RoundSummaryDialog(
    roundResult: RoundResult,
    onDismiss: () -> Unit,
    onNewRound: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        showContent = true
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Animated title
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn() + expandVertically()
                ) {
                    Text(
                        text = when (roundResult.winType) {
                            WinType.MENDIKOT -> "MENDIKOT!"
                            WinType.WHITEWASH -> "WHITEWASH!"
                            WinType.REGULAR -> "Round Complete"
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Winner announcement with scale animation
                var scale by remember { mutableStateOf(0f) }
                LaunchedEffect(showContent) {
                    if (showContent) {
                        animate(0f, 1f, animationSpec = spring()) { value, _ ->
                            scale = value
                        }
                    }
                }
                
                Text(
                    text = "Team ${roundResult.winningTeam + 1} Wins!",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.graphicsLayer(
                        scaleX = scale,
                        scaleY = scale
                    )
                )

                // Score details with slide animation
                AnimatedVisibility(
                    visible = showContent,
                    enter = slideInHorizontally() + fadeIn()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ScoreRow("Team 1", roundResult.team1Tricks, roundResult.team1Tens)
                        ScoreRow("Team 2", roundResult.team2Tricks, roundResult.team2Tens)
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                    Button(onClick = onNewRound) {
                        Text("Next Round")
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreRow(
    teamName: String,
    tricks: Int,
    tens: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(teamName)
        Text("Tricks: $tricks")
        Text("Tens: $tens")
    }
} 