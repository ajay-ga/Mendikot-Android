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
    
    // Celebration emojis based on win type
    val celebrationEmojis = when (roundResult.winType) {
        WinType.MENDIKOT -> "🎯 🎊 👑"
        WinType.WHITEWASH -> "💫 🌟 ⭐"
        WinType.REGULAR -> "🎉 🏆 ✨"
    }
    
    // Rotation animation for the title
    val rotation by animateFloatAsState(
        targetValue = if (showContent) 360f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
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
                // Animated title with emojis
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn() + expandVertically()
                ) {
                    Text(
                        text = when (roundResult.winType) {
                            WinType.MENDIKOT -> "🎯 MENDIKOT! 👑"
                            WinType.WHITEWASH -> "💫 WHITEWASH! ⭐"
                            WinType.REGULAR -> "🎉 Round Complete 🎊"
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.graphicsLayer(rotationZ = rotation)
                    )
                }

                // Winner announcement with scale animation
                var scale by remember { mutableStateOf(0f) }
                LaunchedEffect(showContent) {
                    if (showContent) {
                        animate(0f, 1f, animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )) { value, _ ->
                            scale = value
                        }
                    }
                }
                
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.graphicsLayer(
                        scaleX = scale,
                        scaleY = scale
                    )
                ) {
                    Text(
                        text = "$celebrationEmojis\nTeam ${roundResult.winningTeam + 1} Wins!\n$celebrationEmojis",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                // Score details with slide animation
                AnimatedVisibility(
                    visible = showContent,
                    enter = slideInHorizontally() + fadeIn()
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Final Scores",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            ScoreRow("Team 1", roundResult.team1Tricks, roundResult.team1Tens)
                            ScoreRow("Team 2", roundResult.team2Tricks, roundResult.team2Tens)
                        }
                    }
                }

                // Buttons with fade animation
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(
                        initialAlpha = 0.3f
                    ) + expandVertically()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Close")
                        }
                        Button(
                            onClick = onNewRound,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Next Round 🎮")
                        }
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = teamName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🎴 $tricks",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "🔟 $tens",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 