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
import com.mendikotapp.data.models.Player
import com.mendikotapp.data.models.RoundState
import com.mendikotapp.data.models.Suit
import com.mendikotapp.ui.components.PlayingCard
import com.mendikotapp.viewmodel.GameSetupViewModel
import com.mendikotapp.ui.components.RoundSummaryDialog
import kotlinx.coroutines.delay
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mendikotapp.data.models.GamePhase
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.scale
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

val cardWidth = 60.dp
val cardHeight = 90.dp

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
            .padding(4.dp)  // Reduced padding
    ) {
        PlayingCard(
            card = card,
            onClick = {},
            isTrumpSuit = trumpRevealed && card.suit == trumpSuit,
            modifier = Modifier
                .size(cardWidth, cardHeight)
                .then(
                    when (playerPosition) {
                        1 -> Modifier.rotate(90f)
                        2 -> Modifier.rotate(180f)
                        3 -> Modifier.rotate(270f)
                        else -> Modifier
                    }
                ),
            faceDown = !trumpRevealed
        )
    }
}

@Composable
private fun Scoreboard(
    team1Score: Int,
    team2Score: Int,
    team1Tens: Int,
    team2Tens: Int,
    trumpCard: Card?,
    trumpRevealed: Boolean,
    onRevealTrump: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        Surface(
                            modifier = Modifier
                                .padding(4.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.medium
                                ),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${trumpCard.suit}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Trump Suit",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .padding(start = 4.dp)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Hidden",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        if (trumpCard != null && !trumpRevealed) {
                            Button(
                                onClick = onRevealTrump,
                                modifier = Modifier.padding(top = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Text("Reveal Trump")
                            }
                        }
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
}

@Composable
private fun CurrentPlayerIndicator(
    playerName: String,
    playerTeam: Int,
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
    viewModel: GameSetupViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val gameState by viewModel.gameState.collectAsState()
    val isMultiPlayer = gameState?.gameMode == "manual_4_player"
    val currentPlayer = gameState?.currentPlayer
    var showHistory by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }
    var showRoundSummary by remember { mutableStateOf(true) }

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
            showRoundSummary = true
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
            trumpRevealed = gameState?.trumpRevealed == true,
            onRevealTrump = { viewModel.revealTrump() }
        )

        // Trump selection phase or game play phase
        if (gameState?.trumpCard == null) {
            val rightOfDealer = (gameState?.currentDealer ?: 0 + 1) % 4

            val dealerPlayer = gameState?.players?.get(rightOfDealer)
            if (!isMultiPlayer && dealerPlayer?.isHuman == true) { // Human player's turn to select trump
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
                        playerIndex = 0,
                        isCurrentPlayer = true,
                        onCardClick = { index ->
                            Log.d("GameScreen", "Selected trump card index: $index")
                            viewModel.selectTrump(index)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
            } else if(!isMultiPlayer) {
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
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Center area for current trick
                Box(modifier = Modifier.fillMaxSize()) {
                    CurrentTrick(
                        trick = gameState?.currentTrick ?: emptyList(),
                        trumpSuit = gameState?.trumpCard?.suit,
                        players = gameState?.players ?: emptyList(),
                        currentPlayer = gameState?.currentPlayer ?: 0,
                        gamePhase = gameState?.gamePhase ?: GamePhase.PLAYING,
                        trumpRevealed = gameState?.trumpRevealed ?: false,
                        onCardPlayed = { playerIndex, cardIndex ->
                            if (gameState?.gamePhase == GamePhase.PLAYING) {
                                viewModel.playCard(playerIndex, cardIndex)
                            }
                        },
                        canPlayCard = { playerIndex, card -> 
                            viewModel.canPlayCard(playerIndex, card)
                        },
                        onRevealTrump = {
                            viewModel.revealTrump()
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }

                // Bottom player (Player 0)
                gameState?.players?.get(0)?.let { player ->
                    HorizontalPlayerHand(
                        cards = player.hand,
                        isCurrentPlayer = currentPlayer == 0,
                        onCardSelected = { cardIndex ->
                            if (gameState?.gamePhase == GamePhase.PLAYING) {
                                viewModel.playCard(0, cardIndex)
                            }
                        },
                        rotationDegrees = 0f,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        playerName = player.name,
                        hasPlayedCard = gameState?.currentTrick?.any { it.first == 0 } == true,
                        isHuman = player.isHuman,
                        canPlayCard = { card -> 
                            viewModel.canPlayCard(0, card)
                        },
                        gamePhase = gameState?.gamePhase ?: GamePhase.PLAYING,
                        trumpRevealed = gameState?.trumpRevealed ?: false,
                        onRevealTrump = { viewModel.revealTrump() }
                    )
                }

                // Left player (Player 1)
                gameState?.players?.get(1)?.let { player ->
                    HorizontalPlayerHand(
                        cards = player.hand,
                        isCurrentPlayer = currentPlayer == 1,
                        onCardSelected = { cardIndex ->
                            if (gameState?.gamePhase == GamePhase.PLAYING) {
                                viewModel.playCard(1, cardIndex)
                            }
                        },
                        rotationDegrees = 90f,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(start = 8.dp),
                        playerName = player.name,
                        hasPlayedCard = gameState?.currentTrick?.any { it.first == 1 } == true,
                        isHuman = player.isHuman,
                        canPlayCard = { card -> 
                            viewModel.canPlayCard(1, card)
                        },
                        gamePhase = gameState?.gamePhase ?: GamePhase.PLAYING,
                        trumpRevealed = gameState?.trumpRevealed ?: false,
                        onRevealTrump = { viewModel.revealTrump() }
                    )
                }

                // Top player (Player 2)
                gameState?.players?.get(2)?.let { player ->
                    HorizontalPlayerHand(
                        cards = player.hand,
                        isCurrentPlayer = currentPlayer == 2,
                        onCardSelected = { cardIndex ->
                            if (gameState?.gamePhase == GamePhase.PLAYING) {
                                viewModel.playCard(2, cardIndex)
                            }
                        },
                        rotationDegrees = 180f,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(
                                top = 16.dp,
                                start = 16.dp
                            ),
                        playerName = player.name,
                        hasPlayedCard = gameState?.currentTrick?.any { it.first == 2 } == true,
                        isHuman = player.isHuman,
                        canPlayCard = { card -> 
                            viewModel.canPlayCard(2, card)
                        },
                        gamePhase = gameState?.gamePhase ?: GamePhase.PLAYING,
                        trumpRevealed = gameState?.trumpRevealed ?: false,
                        onRevealTrump = { viewModel.revealTrump() }
                    )
                }

                // Right player (Player 3)
                gameState?.players?.get(3)?.let { player ->
                    HorizontalPlayerHand(
                        cards = player.hand,
                        isCurrentPlayer = currentPlayer == 3,
                        onCardSelected = { cardIndex ->
                            if (gameState?.gamePhase == GamePhase.PLAYING) {
                                viewModel.playCard(3, cardIndex)
                            }
                        },
                        contentPadding = PaddingValues(
                            horizontal = 48.dp
                        ),
                        rotationDegrees = 270f,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(
                                end = 8.dp,
                                top = 120.dp
                            ),
                        playerName = player.name,
                        hasPlayedCard = gameState?.currentTrick?.any { it.first == 3 } == true,
                        isHuman = player.isHuman,
                        canPlayCard = { card -> 
                            viewModel.canPlayCard(3, card)
                        },
                        gamePhase = gameState?.gamePhase ?: GamePhase.PLAYING,
                        trumpRevealed = gameState?.trumpRevealed ?: false,
                        onRevealTrump = { viewModel.revealTrump() }
                    )
                }

                // Current player indicator
//                if (isMultiPlayer && currentPlayer != null) {
//                    CurrentPlayerIndicator(
//                        playerName = gameState?.players?.get(currentPlayer)?.name ?: "",
//                        playerTeam = currentPlayer % 2,
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                }
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

    if (showRoundSummary && gameState?.roundState == RoundState.COMPLETED) {
        gameState?.roundHistory?.lastOrNull()?.let { result ->
            RoundSummaryDialog(
                roundResult = result,
                onDismiss = {
                    // Navigate to home and clear backstack
                    navController.navigate("game_mode_selection") {
                        popUpTo("game_mode_selection") { inclusive = true }
                    }
                },
                onNewRound = {
                    showRoundSummary = false
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
    cards: List<Card>,
    playerIndex: Int,
    isCurrentPlayer: Boolean,
    onCardClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if(!isCurrentPlayer) {
        return
    }
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy((-16).dp),
        contentPadding = PaddingValues(horizontal = 32.dp)
    ) {
        items(cards.size) { index ->
            PlayingCard(
                card = cards[index],
                onClick = { if (isCurrentPlayer) onCardClick(index) },
                modifier = Modifier.padding(4.dp),
                faceDown = !isCurrentPlayer
            )
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

@Composable
private fun TeamInfo(
    team1Players: List<String>,
    team2Players: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Team 1 info
        Surface(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.weight(1f).padding(4.dp)
        ) {
            Text(
                text = "Team 1: ${team1Players.joinToString(" & ")}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
        }
        
        // Team 2 info
        Surface(
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.weight(1f).padding(4.dp)
        ) {
            Text(
                text = "Team 2: ${team2Players.joinToString(" & ")}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TrickResultDisplay(
    winningPlayer: String,
    winningTeam: Int,
    currentPlayerTeam: Int,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }
    val isWinner = winningTeam == currentPlayerTeam
    
    // Animation for scaling
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    // Rotation animation for winner
    val rotation by animateFloatAsState(
        targetValue = if (isVisible && isWinner) 360f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    LaunchedEffect(Unit) {
        // Blink effect
        repeat(3) {
            delay(300)
            isVisible = !isVisible
            delay(300)
            isVisible = !isVisible
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (isVisible) {
            Surface(
                modifier = Modifier
                    .padding(8.dp)
                    .scale(scale)
                    .graphicsLayer(rotationZ = if (isWinner) rotation else 0f),
                shape = MaterialTheme.shapes.medium,
                color = when {
                    isWinner -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }.copy(alpha = 0.9f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Winner emojis and animations
                    if (isWinner) {
                        Text(
                            text = "🎉",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    
                    // Show different icons based on win/loss
                    Icon(
                        imageVector = if (isWinner) 
                            Icons.Filled.Star 
                        else 
                            Icons.Filled.Info,
                        contentDescription = null,
                        tint = if (isWinner) 
                            MaterialTheme.colorScheme.onTertiary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isWinner) 
                            "$winningPlayer wins! 🏆" 
                        else 
                            "Team ${winningTeam + 1} takes the trick",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isWinner) 
                            MaterialTheme.colorScheme.onTertiary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isWinner) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✨",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentTrick(
    trick: List<Pair<Int, Card>>,
    trumpSuit: Suit?,
    players: List<Player>,
    currentPlayer: Int,
    gamePhase: GamePhase,
    trumpRevealed: Boolean,
    onCardPlayed: (Int, Int) -> Unit,
    canPlayCard: (Int, Card) -> Boolean,
    onRevealTrump: () -> Unit,
    modifier: Modifier = Modifier
) {
    if(gamePhase == GamePhase.COMPLETED) {
        return
    }

    // Add state for winner highlight
    var winningPlayerIndex by remember { mutableStateOf<Int?>(null) }
    val scale by animateFloatAsState(
        targetValue = if (winningPlayerIndex != null) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    // Check for trick completion
    LaunchedEffect(trick) {
        if (trick.size == 4) {
            // Determine winner based on trick rules
            val leadSuit = trick.first().second.suit
            val winningCard = trick.maxByOrNull { (_, card) ->
                when {
                    card.suit == trumpSuit -> 100 + card.value
                    card.suit == leadSuit -> card.value
                    else -> -1
                }
            }
            winningPlayerIndex = winningCard?.first
            
            // Reset winner highlight after delay
            delay(1500)
            winningPlayerIndex = null
        } else {
            winningPlayerIndex = null
        }
    }

    Box(modifier = modifier) {
        (0..3).forEach { playerIndex ->
            val playedCard = trick.find { it.first == playerIndex }
            Box(
                modifier = when (playerIndex) {
                    0 -> Modifier.align(Alignment.BottomCenter)
                    1 -> Modifier.align(Alignment.CenterEnd)
                    2 -> Modifier.align(Alignment.TopCenter)
                    3 -> Modifier.align(Alignment.CenterStart)
                    else -> Modifier.align(Alignment.Center)
                }
            ) {
                if (playedCard != null) {
                    PlayingCard(
                        card = playedCard.second,
                        onClick = {},
                        modifier = Modifier
                            .size(cardWidth, cardHeight)
                            .padding(
                                when (playerIndex) {
                                    2 -> PaddingValues(0.dp)
                                    else -> PaddingValues(0.dp)
                                }
                            )
                            .then(
                                if (playerIndex == winningPlayerIndex) {
                                    Modifier
                                        .scale(scale)
                                        .border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = MaterialTheme.shapes.medium
                                        )
                                } else Modifier
                            ),
                        isTrumpSuit = playedCard.second.suit == trumpSuit
                    )
                } else {
                    PlayerPlaceholder(
                        playerName = players[playerIndex].name,
                        cardCount = players[playerIndex].hand.size,
                        isCurrentPlayer = currentPlayer == playerIndex,
                        modifier = Modifier.width(cardWidth * 1.2f),
                        cards = players[playerIndex].hand,
                        onCardSelected = { index -> onCardPlayed(playerIndex, index) },
                        isHuman = players[playerIndex].isHuman,
                        canPlayCard = { card -> canPlayCard(playerIndex, card) },
                        gamePhase = gamePhase,
                        trumpRevealed = trumpRevealed,
                        onRevealTrump = onRevealTrump
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerHandDisplay(
    cards: List<Card>,
    isCurrentPlayer: Boolean,
    onCardSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy((-15).dp),
            contentPadding = PaddingValues(horizontal = 24.dp),
            userScrollEnabled = true,  // Enable scrolling
            state = rememberLazyListState()  // Add state for better scroll control
        ) {
            items(cards.size) { index ->
                PlayingCard(
                    card = cards[index],
                    onClick = { if (isCurrentPlayer) onCardSelected(index) },
                    modifier = Modifier
                        .size(cardWidth, cardHeight)
                        .padding(2.dp),
                    faceDown = !isCurrentPlayer
                )
            }
        }
    }
}

@Composable
private fun PlayerPlaceholder(
    playerName: String,
    cardCount: Int,
    isCurrentPlayer: Boolean,
    modifier: Modifier = Modifier,
    cards: List<Card> = emptyList(),
    onCardSelected: (Int) -> Unit = {},
    isHuman: Boolean = false,
    canPlayCard: (Card) -> Boolean = { true },
    gamePhase: GamePhase = GamePhase.PLAYING,
    trumpRevealed: Boolean = false,
    onRevealTrump: () -> Unit = {}
) {
    var showHandDialog by remember { mutableStateOf(false) }
    var showInvalidCardMessage by remember { mutableStateOf(false) }

    // Reset dialog state when game phase changes
    LaunchedEffect(gamePhase) {
        if (gamePhase == GamePhase.COMPLETED) {
            showHandDialog = false
            showInvalidCardMessage = false
        }
    }

    Surface(
        modifier = modifier
            .padding(0.dp)
            .height(cardHeight)
            .fillMaxWidth(0.3f)
            .clickable(enabled = isCurrentPlayer && isHuman && gamePhase != GamePhase.COMPLETED) {
                showHandDialog = true
            },
        color = if (isCurrentPlayer) 
            MaterialTheme.colorScheme.primaryContainer 
        else 
            MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            width = if (isCurrentPlayer) 2.dp else 1.dp,
            color = if (isCurrentPlayer) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.outline
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = playerName,
                style = MaterialTheme.typography.titleMedium,
                color = if (isCurrentPlayer)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$cardCount cards",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCurrentPlayer)
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }

    if (showHandDialog && isCurrentPlayer && isHuman && gamePhase != GamePhase.COMPLETED) {
        if(cards.isEmpty()) {
            return
        }
        Dialog(
            onDismissRequest = { 
                if (gamePhase != GamePhase.COMPLETED) {
                    showHandDialog = false 
                }
            }
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$playerName Turn",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (gamePhase == GamePhase.PLAYING && !trumpRevealed) {
                        Button(
                            onClick = onRevealTrump,
                            modifier = Modifier.padding(vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text("Reveal Trump")
                        }
                    }

                    if (showInvalidCardMessage) {
                        Text(
                            text = "Invalid card! Please select another card.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy((-15).dp),
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        userScrollEnabled = true
                    ) {
                        items(cards.size) { index ->
                            val card = cards[index]
                            PlayingCard(
                                card = card,
                                onClick = { 
                                    if (canPlayCard(card)) {
                                        onCardSelected(index)
                                        showHandDialog = false
                                        showInvalidCardMessage = false
                                    } else {
                                        showInvalidCardMessage = true
                                    }
                                },
                                modifier = Modifier
                                    .size(cardWidth, cardHeight)
                                    .padding(2.dp),
                                faceDown = false
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HorizontalPlayerHand(
    cards: List<Card>,
    isCurrentPlayer: Boolean,
    onCardSelected: (Int) -> Unit,
    rotationDegrees: Float,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp),
    modifier: Modifier = Modifier,
    playerName: String = "",
    hasPlayedCard: Boolean = false,
    isHuman: Boolean = false,
    canPlayCard: (Card) -> Boolean = { true },
    gamePhase: GamePhase = GamePhase.PLAYING,
    trumpRevealed: Boolean = false,
    onRevealTrump: () -> Unit = {}
) {
    var showHandDialog by remember { mutableStateOf(false) }
    var showInvalidCardMessage by remember { mutableStateOf(false) }

    // Reset dialog state when game phase changes
    LaunchedEffect(gamePhase) {
        if (gamePhase == GamePhase.COMPLETED) {
            showHandDialog = false
            showInvalidCardMessage = false
        }
    }

    // Show dialog when it becomes player's turn
    LaunchedEffect(isCurrentPlayer, hasPlayedCard, trumpRevealed, gamePhase) {
        showHandDialog = isCurrentPlayer && isHuman && !hasPlayedCard && 
            gamePhase != GamePhase.COMPLETED &&  
            (gamePhase == GamePhase.PLAYING || 
            (gamePhase == GamePhase.TRUMP_SELECTION && trumpRevealed))
    }

    if (showHandDialog && isCurrentPlayer && isHuman && !hasPlayedCard && gamePhase != GamePhase.COMPLETED) {
        Dialog(
            onDismissRequest = { 
                if (gamePhase != GamePhase.COMPLETED) {
                    showHandDialog = false 
                }
            }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$playerName Turn",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Add reveal trump button
                    if (gamePhase == GamePhase.PLAYING && !trumpRevealed) {
                        Button(
                            onClick = onRevealTrump,
                            modifier = Modifier.padding(vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text("Reveal Trump")
                        }
                    }

                    if (showInvalidCardMessage) {
                        Text(
                            text = "Invalid card! Please select another card.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy((-15).dp),
                        contentPadding = contentPadding,
                        userScrollEnabled = true
                    ) {
                        items(cards.size) { index ->
                            val card = cards[index]
                            PlayingCard(
                                card = card,
                                onClick = { 
                                    if (canPlayCard(card)) {
                                        onCardSelected(index)
                                        showHandDialog = false
                                        showInvalidCardMessage = false
                                    } else {
                                        showInvalidCardMessage = true
                                    }
                                },
                                modifier = Modifier
                                    .size(cardWidth, cardHeight)
                                    .padding(2.dp),
                                faceDown = false
                            )
                        }
                    }
                }
            }
        }
    }
} 