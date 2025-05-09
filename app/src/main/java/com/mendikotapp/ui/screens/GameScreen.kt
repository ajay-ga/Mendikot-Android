package com.mendikotapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mendikotapp.ui.components.PlayingCard
import com.mendikotapp.viewmodel.GameSetupViewModel

@Composable
fun GameScreen(
    viewModel: GameSetupViewModel = hiltViewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top player (Player 2)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy((-30).dp)
            ) {
                repeat(13) {
                    PlayingCard(
                        card = gameState?.players?.get(2)?.hand?.get(it) 
                            ?: return@repeat,
                        faceDown = true
                    )
                }
            }
        }

        // Left player (Player 1)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy((-60).dp)
            ) {
                repeat(13) {
                    PlayingCard(
                        card = gameState?.players?.get(1)?.hand?.get(it) 
                            ?: return@repeat,
                        faceDown = true,
                        modifier = Modifier.rotate(90f)
                    )
                }
            }
        }

        // Right player (Player 3)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy((-60).dp)
            ) {
                repeat(13) {
                    PlayingCard(
                        card = gameState?.players?.get(3)?.hand?.get(it) 
                            ?: return@repeat,
                        faceDown = true,
                        modifier = Modifier.rotate(-90f)
                    )
                }
            }
        }

        // Bottom player (Human - Player 0)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy((-30).dp)
            ) {
                gameState?.players?.get(0)?.hand?.forEach { card ->
                    PlayingCard(card = card)
                }
            }
        }

        // Center area for current trick
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(200.dp)
        ) {
            gameState?.currentTrick?.forEachIndexed { index, card ->
                PlayingCard(
                    card = card,
                    modifier = Modifier.align(
                        when (index) {
                            0 -> Alignment.BottomCenter
                            1 -> Alignment.CenterStart
                            2 -> Alignment.TopCenter
                            3 -> Alignment.CenterEnd
                            else -> Alignment.Center
                        }
                    )
                )
            }
        }
    }
} 