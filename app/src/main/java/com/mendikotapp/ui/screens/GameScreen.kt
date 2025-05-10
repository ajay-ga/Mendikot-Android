package com.mendikotapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mendikotapp.R
import com.mendikotapp.data.models.Card
import com.mendikotapp.data.models.RoundState
import com.mendikotapp.ui.components.PlayingCard
import com.mendikotapp.viewmodel.GameSetupViewModel
import com.mendikotapp.ui.components.RoundSummaryDialog
import kotlinx.coroutines.delay
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mendikotapp.data.models.GamePhase
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import com.mendikotapp.data.models.Suit

@Composable
private fun TrumpCard(
    card: Card,
    playerPosition: Int,
    trumpSuit: Suit? = null,
    trumpRevealed: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(8.dp)
    ) {
//        PlayingCard(
//            card = card,
//            isTrumpSuit = trumpRevealed && card.suit == trumpSuit,  // Highlight if it's the trump suit
//            modifier = when (playerPosition) {
//                1 -> Modifier.rotate(90f)  // Left player
//                2 -> Modifier.rotate(180f) // Top player
//                3 -> Modifier.rotate(270f) // Right player
//                else -> Modifier          // Bottom player
//            },
//            faceDown = true
//
//        )
    }
}

@Composable
private fun Scoreboard(
    team1Score: Int,
    team2Score: Int,
    team1Tens: Int,
    team2Tens: Int,
    trumpCard: Card?,
    trumpRevealed: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Team 1 Score
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.team_1),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$team1Score",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "(${team1Tens} tens)",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Trump info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Trump",
                    style = MaterialTheme.typography.titleMedium
                )
                if (trumpRevealed && trumpCard != null) {
                    Text(
                        text = "${trumpCard.suit}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "Hidden",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Team 2 Score
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.team_2),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$team2Score",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "(${team2Tens} tens)",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun CurrentPlayerIndicator(
    playerName: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = "Current Player: $playerName",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun GameScreen(
    viewModel: GameSetupViewModel
) {
    val gameState by viewModel.gameState.collectAsState()
    var showHistory by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }

    // Check if we need to initialize the game
    LaunchedEffect(Unit) {
        if (gameState == null) {
            Log.d("GameScreen", "Navigated to game without initialization")
            // Navigate back to setup or handle error
        }
        Log.d("GameScreen", "Initial game state: $gameState")
    }

    LaunchedEffect(gameState?.roundState) {
        if (gameState?.roundState == RoundState.COMPLETED) {
            showCelebration = true
            delay(500)
        }
    }

    // Add state observer
    DisposableEffect(Unit) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                Log.d("GameScreen", "Screen resumed, game state: $gameState")
            }
        }
        
        onDispose {
            Log.d("GameScreen", "Screen disposed")
        }
    }

    Log.d("GameScreen", gameState.toString())
    if (gameState == null) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Main game layout
    Column(modifier = Modifier.fillMaxSize()) {
        // Scoreboard
        Scoreboard(
            team1Score = gameState?.team1Score ?: 0,
            team2Score = gameState?.team2Score ?: 0,
            team1Tens = gameState?.team1Tens ?: 0,
            team2Tens = gameState?.team2Tens ?: 0,
            trumpCard = gameState?.trumpCard,
            trumpRevealed = gameState?.trumpRevealed == true
        )

        // Trump selection phase or game play phase
        if (gameState?.trumpCard == null) {
            val rightOfDealer = (gameState?.currentDealer ?: 0 + 1) % 4
            if (rightOfDealer == 0) { // Human player's turn to select trump
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Select a card as trump",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    PlayerHand(
                        cards = gameState?.players?.get(0)?.hand ?: emptyList(),
                        playerName = gameState?.players?.get(0)?.name ?: "",
                        isCurrentPlayer = true,
                        faceDown = false,
                        onCardClick = { index ->
                            Log.d("GameScreen", "Selected trump card index: $index")
                            viewModel.selectTrump(index)
                        }
                    )
                }
            } else {
                // Show waiting message for bot trump selection
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Waiting for ${gameState?.players?.get(rightOfDealer)?.name} to select trump...",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Team information
                TeamInfo(
                    team1Players = listOf(
                        gameState?.players?.get(0)?.name ?: "",
                        gameState?.players?.get(2)?.name ?: ""
                    ),
                    team2Players = listOf(
                        gameState?.players?.get(1)?.name ?: "",
                        gameState?.players?.get(3)?.name ?: ""
                    )
                )
                
                // Current player indicator
                CurrentPlayerIndicator(
                    playerName = gameState?.players?.get(gameState?.currentPlayer ?: 0)?.name ?: "",
                    playerTeam = (gameState?.currentPlayer ?: 0) % 2
                )
            }

            // Game play area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Show trump card at the selector's position
                val rightOfDealer = (gameState?.currentDealer ?: 0 + 1) % 4
                gameState?.trumpCard?.let { trumpCard ->
                    TrumpCard(
                        card = trumpCard,
                        playerPosition = rightOfDealer,
                        trumpSuit = gameState?.trumpCard?.suit,
                        trumpRevealed = gameState?.trumpRevealed == true,
                        modifier = when (rightOfDealer) {
                            0 -> Modifier  // Bottom player
                                .align(Alignment.BottomCenter)
                                .offset(x = 120.dp)  // Move right of the hand
                            1 -> Modifier  // Left player
                                .align(Alignment.CenterStart)
                                .offset(y = 120.dp)  // Move below the hand
                            2 -> Modifier  // Top player
                                .align(Alignment.TopCenter)
                                .offset(x = (-120).dp)  // Move left of the hand
                            3 -> Modifier  // Right player
                                .align(Alignment.CenterEnd)
                                .offset(y = (-120).dp)  // Move above the hand
                            else -> Modifier.align(Alignment.Center)
                        }
                    )
                }

                // Player hands
                // Top player (Player 2)
                PlayerHand(
                    modifier = Modifier.align(Alignment.TopCenter),
                    cards = gameState?.players?.get(2)?.hand ?: emptyList(),
                    playerName = gameState?.players?.get(2)?.name ?: "",
                    isCurrentPlayer = gameState?.currentPlayer == 2,
                    faceDown = true,
                    isVertical = true,
                    trumpSuit = gameState?.trumpCard?.suit,
                    trumpRevealed = gameState?.trumpRevealed == true
                )

                // Left player (Player 1)
                PlayerHand(
                    modifier = Modifier.align(Alignment.CenterStart),
                    cards = gameState?.players?.get(1)?.hand ?: emptyList(),
                    playerName = gameState?.players?.get(1)?.name ?: "",
                    isCurrentPlayer = gameState?.currentPlayer == 1,
                    faceDown = true,
                    isVertical = true,
                    trumpSuit = gameState?.trumpCard?.suit,
                    trumpRevealed = gameState?.trumpRevealed == true
                )

                // Right player (Player 3)
                PlayerHand(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    cards = gameState?.players?.get(3)?.hand ?: emptyList(),
                    playerName = gameState?.players?.get(3)?.name ?: "",
                    isCurrentPlayer = gameState?.currentPlayer == 3,
                    faceDown = true,
                    isVertical = true,
//                    rotationDegrees = -90f,
                    trumpSuit = gameState?.trumpCard?.suit,
                    trumpRevealed = gameState?.trumpRevealed == true
                )

                // Bottom player (Human - Player 0)
                PlayerHand(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    cards = gameState?.players?.get(0)?.hand ?: emptyList(),
                    playerName = gameState?.players?.get(0)?.name ?: "",
                    isCurrentPlayer = gameState?.currentPlayer == 0,
                    faceDown = false,
                    trumpSuit = gameState?.trumpCard?.suit,
                    trumpRevealed = gameState?.trumpRevealed == true,
                    onCardClick = if (gameState?.currentPlayer == 0 && gameState?.gamePhase == GamePhase.PLAYING) {
                        { index -> viewModel.playCard(0, index) }
                    } else null
                )

                // Current trick cards
                gameState?.currentTrick?.forEach { (playerIndex, card) ->
                    PlayingCard(
                        card = card,
                        modifier = when (playerIndex) {
                            0 -> Modifier.align(Alignment.BottomCenter)
                            1 -> Modifier.align(Alignment.CenterStart)
                            2 -> Modifier.align(Alignment.TopCenter)
                            3 -> Modifier.align(Alignment.CenterEnd)
                            else -> Modifier.align(Alignment.Center)
                        }
                    )
                }
            }
        }

        // Action buttons in a surface
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!gameState?.trumpRevealed!! && gameState?.currentPlayer == 0) {
                    Button(
                        onClick = { viewModel.revealTrump() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Reveal Trump")
                    }
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(onClick = { showHistory = true }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(R.string.show_history)
                    )
                }
            }
        }
    }

    // Celebration and dialogs
    if (showCelebration) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
        )
    }

    if (gameState?.roundState == RoundState.COMPLETED) {
        gameState?.roundHistory?.lastOrNull()?.let { result ->
            RoundSummaryDialog(
                roundResult = result,
                onDismiss = { showCelebration = false },
                onNewRound = {
                    showCelebration = false
                    viewModel.startNewRound()
                }
            )
        }
    }

    if (showHistory) {
        AlertDialog(
            onDismissRequest = { showHistory = false },
            title = { Text(stringResource(R.string.trick_history)) },
            text = {
                Column {
                    gameState?.completedTricks?.forEachIndexed { trickIndex, trick ->
                        Text("Trick ${trickIndex + 1}:")
                        trick.forEach { (playerIndex, card) ->
                            Text("  ${gameState?.players?.get(playerIndex)?.name}: $card")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHistory = false }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }
}

@Composable
private fun PlayerHand(
    modifier: Modifier = Modifier,
    cards: List<Card>,
    playerName: String,
    isCurrentPlayer: Boolean,
    faceDown: Boolean,
    isVertical: Boolean = false,
    rotationDegrees: Float = if (isVertical) 0f else 0f,
    trumpSuit: Suit? = null,
    trumpRevealed: Boolean = false,
    onCardClick: ((Int) -> Unit)? = null
) {
    if (isVertical) {
        VerticalPlayerDisplay(
            playerName = playerName,
            cardCount = cards.size,
            isCurrentPlayer = isCurrentPlayer,
            rotationDegrees = rotationDegrees,
            modifier = modifier
        )
    } else {
        val containerModifier = modifier.then(
            if (isCurrentPlayer) {
                Modifier.border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                )
            } else Modifier
        )

        Box(
            modifier = containerModifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center  // Center the entire LazyRow
        ) {
            LazyRow(
                modifier = Modifier
                    .padding(8.dp)
                    .wrapContentWidth(),  // Only take needed width
                horizontalArrangement = Arrangement.spacedBy((-50).dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items(cards.size) { index ->
                    PlayingCard(
                        card = cards[index],
                        faceDown = faceDown,
                        isTrumpSuit = !faceDown && trumpRevealed && cards[index].suit == trumpSuit,
                        onClick = if (!faceDown && onCardClick != null) {
                            { onCardClick(index) }
                        } else null
                    )
                }
            }
        }
    }
}

// Add new composable for vertical player display
@Composable
private fun VerticalPlayerDisplay(
    playerName: String,
    cardCount: Int,
    isCurrentPlayer: Boolean,
    rotationDegrees: Float = 90f,
    modifier: Modifier = Modifier
) {
    val containerModifier = modifier.then(
        if (isCurrentPlayer) {
            Modifier.border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            )
        } else Modifier
    )

    Column(
        modifier = containerModifier
            .padding(16.dp)
            .rotate(rotationDegrees),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Player icon
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = playerName.first().toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // Player name and card count
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = playerName,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$cardCount cards",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 