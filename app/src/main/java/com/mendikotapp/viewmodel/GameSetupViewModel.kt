package com.mendikotapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.mendikotapp.data.models.Card
import com.mendikotapp.data.models.GameState
import com.mendikotapp.data.models.Player
import com.mendikotapp.data.repository.GameRepository
import com.mendikotapp.data.repository.TrickPlayingRepository
import com.mendikotapp.data.ai.BotAI
import com.mendikotapp.data.models.GamePhase
import com.mendikotapp.data.models.RoundState
import com.mendikotapp.data.scoring.RoundTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GameSetupViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val trickPlayingRepository: TrickPlayingRepository,
    private val botAI: BotAI,
    private val roundTracker: RoundTracker
) : ViewModel() {
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()
    
    private val _gameMode = MutableStateFlow<String?>(null)
    val gameMode: StateFlow<String?> = _gameMode.asStateFlow()
    
    private val viewModelScope = CoroutineScope(Dispatchers.Main + Job())
    
    init {
        // Log when ViewModel is created
        Log.d("GameSetupViewModel", "ViewModel initialized")
    }
    
    fun setGameMode(mode: String) {
        _gameMode.value = mode
    }
    
    fun initializeGame(
        player1Name: String,
        player2Name: String = "Bot 1",
        player3Name: String = "Bot 2",
        player4Name: String = "Bot 3"
    ) {
        val isMultiPlayer = _gameMode.value == "manual_4_player"
        
        // Create players based on mode
        val players = listOf(
            Player(
                id = 0,
                name = player1Name,
                isHuman = true,
                team = 0,
                _hand = mutableListOf()
            ),
            Player(
                id = 1,
                name = player2Name,
                isHuman = isMultiPlayer,
                team = 1,
                _hand = mutableListOf()
            ),
            Player(
                id = 2,
                name = player3Name,
                isHuman = isMultiPlayer,
                team = 0,
                _hand = mutableListOf()
            ),
            Player(
                id = 3,
                name = player4Name,
                isHuman = isMultiPlayer,
                team = 1,
                _hand = mutableListOf()
            )
        )

        // Initialize new game state with the game mode
        _gameState.value = GameState(
            players = players,
            currentDealer = Random.nextInt(4),
            currentPlayer = 0,
            gamePhase = GamePhase.TRUMP_SELECTION,
            gameMode = _gameMode.value ?: "single_player"  // Set the game mode
        )

        // Deal cards
        gameRepository.dealCards(players)
        Log.d("GameSetupViewModel", "Cards dealt. Player 0 hand size: ${players[0].hand.size}")
        
        // Update state after dealing
        _gameState.value = _gameState.value?.copy(
            players = players
        )
        
        // If the player to dealer's right is a bot, have them select trump immediately
        val rightOfDealer = (_gameState.value?.currentDealer ?: 0 + 1) % 4
        if (!players[rightOfDealer].isHuman) {
            Log.d("GameSetupViewModel", "Bot selecting trump")
            // Simulate bot selecting trump after a short delay
            handleBotTrumpSelection(rightOfDealer)
        }
        
        Log.d("GameSetupViewModel", "Game initialization complete. Current state: ${_gameState.value}")
    }
    
    private fun handleBotTrumpSelection(botIndex: Int) {
        viewModelScope.launch {
            val currentState = _gameState.value ?: run {
                Log.e("GameSetupViewModel", "Current state is null during bot trump selection")
                return@launch
            }
            
            val botPlayer = currentState.players[botIndex]
            Log.d("GameSetupViewModel", "Bot ${botPlayer.name} selecting trump")
            
            // Add delay before trump selection
            delay(3000) // 3 second delay
            
            val trumpIndex = botPlayer.hand.indexOfFirst { !it.isTen }
            if (trumpIndex == -1) {
                Log.e("GameSetupViewModel", "Bot couldn't find a non-ten card for trump")
                return@launch
            }
            
            selectTrump(trumpIndex)
        }
    }
    
    fun selectTrump(cardIndex: Int) {
        viewModelScope.launch {
            Log.d("GameSetupViewModel", "=== Selecting Trump ===")
            
            val currentState = _gameState.value ?: run {
                Log.e("GameSetupViewModel", "Current state is null during trump selection")
                return@launch
            }
            
            val rightOfDealer = (currentState.currentDealer + 1) % 4
            val trumpSelector = currentState.players[rightOfDealer]
            
            // Add initial delay before selecting trump
            delay(1000)
            
            val trumpCard = gameRepository.selectTrumpCard(trumpSelector, cardIndex)
            Log.d("GameSetupViewModel", "Trump card selected: $trumpCard")
            
            // Create new state with trump card and next player
            val newState = currentState.copy(
                trumpCard = trumpCard,
                currentPlayer = (rightOfDealer + 1) % 4,
                players = currentState.players,
                gamePhase = GamePhase.PLAYING
            )
            

            
            // Add longer delay after trump selection to show the selected card
            delay(3000)
            _gameState.value = newState
            
            // Start bot moves if next player is a bot
            if (!newState.players[newState.currentPlayer].isHuman) {
                handleBotMoves()
            } else {
                if (roundTracker.isRoundComplete(newState)) {
                    Log.d("GameSetupViewModel", "Round completed")
                    val result = roundTracker.determineRoundResult(newState)
                    val newState1 = newState.copy(
                        roundState = RoundState.COMPLETED,
                        roundHistory = newState.roundHistory + result
                    )
                    _gameState.value = newState1
                    delay(2000) // Extra delay at round end
//                    break
                }
            }


        }
    }
    
    fun playCard(playerIndex: Int, cardIndex: Int) {
        viewModelScope.launch {
            val currentState = _gameState.value ?: run {
                Log.e("GameSetupViewModel", "Current state is null during play card")
                return@launch
            }

            Log.d("GameSetupViewModel", "=== Before Playing Card ===")
            currentState.players.forEachIndexed { index, player ->
                Log.d("GameSetupViewModel", "Player $index hand size: ${player.hand.size}")
            }
            Log.d("GameSetupViewModel", "Current trick size: ${currentState.currentTrick.size}")
            
            // Play the card and update state
            val playedState = trickPlayingRepository.playCard(currentState, playerIndex, cardIndex)
            
            Log.d("GameSetupViewModel", "=== After Playing Card ===")
            playedState.players.forEachIndexed { index, player ->
                Log.d("GameSetupViewModel", "Player $index hand size: ${player.hand.size}")
            }
            Log.d("GameSetupViewModel", "Current trick size: ${playedState.currentTrick.size}")
            
            _gameState.value = playedState

            // If trick is complete, handle completion
            if (playedState.currentTrick.size == 4) {
                delay(2000) // Show completed trick
                
                Log.d("GameSetupViewModel", "=== Completing Trick ===")
                // Create new state with empty trick and reset ledSuit
                val nextState = playedState.copy(
                    currentTrick = emptyList(),
                    completedTricks = playedState.completedTricks + listOf(playedState.currentTrick),
                    ledSuit = null  // Reset ledSuit for next trick
                )
                
                nextState.players.forEachIndexed { index, player ->
                    Log.d("GameSetupViewModel", "Player $index hand size: ${player.hand.size}")
                }
                
                _gameState.value = nextState

                // Check for round completion
                if (roundTracker.isRoundComplete(nextState)) {
                    val result = roundTracker.determineRoundResult(nextState)
                    val finalState = nextState.copy(
                        roundState = RoundState.COMPLETED,
                        roundHistory = nextState.roundHistory + result
                    )
                    _gameState.value = finalState
                    delay(1000)
                } else {
                    // Continue with bot moves if next player is a bot
                    if (!nextState.players[nextState.currentPlayer].isHuman) {
                        handleBotMoves()
                    }
                }
            } else {
                // Handle bot moves for incomplete trick if next player is a bot
                if (!playedState.players[playedState.currentPlayer].isHuman) {
                    handleBotMoves()
                }
            }
        }
    }
    
    private fun handleBotMoves() {
        viewModelScope.launch {
            var state = _gameState.value ?: return@launch
            
            while (!state.players[state.currentPlayer].isHuman && 
                   state.roundState != RoundState.COMPLETED) {
                
                val botPlayer = state.players[state.currentPlayer]
                Log.d("GameSetupViewModel", "Bot ${botPlayer.name} thinking...")
                
                // Check if bot should reveal trump
                if (!state.trumpRevealed && 
                    botAI.shouldRevealTrump(state, state.currentPlayer)) {
                    Log.d("GameSetupViewModel", "Bot ${botPlayer.name} revealing trump")
                    state = trickPlayingRepository.revealTrump(state)
                    _gameState.value = state
                    delay(1000) // Delay after revealing trump
                }

                // Let bot choose a card
                val cardIndex = botAI.selectCardToPlay(state, state.currentPlayer)
                
                if (cardIndex == -1) {
                    Log.e("GameSetupViewModel", "Bot couldn't select a card!")
                    break
                }

                // Play the card
                state = trickPlayingRepository.playCard(state, state.currentPlayer, cardIndex)
                _gameState.value = state
                
                // Add delay after each bot move
                delay(1500) // 1.5 seconds delay
                
                // Handle trick completion
                if (state.currentTrick.size == 4) {
                    delay(2000) // Show completed trick
                    
                    // Create new state with empty trick
                    state = state.copy(
                        currentTrick = emptyList(),
                        completedTricks = state.completedTricks + listOf(state.currentTrick),
                        ledSuit = null
                    )
                    _gameState.value = state
                    
                    // Check for round completion
                    if (roundTracker.isRoundComplete(state)) {
                        val result = roundTracker.determineRoundResult(state)
                        state = state.copy(
                            roundState = RoundState.COMPLETED,
                            roundHistory = state.roundHistory + result
                        )
                        _gameState.value = state
                        break
                    }
                }
            }
        }
    }
    
    fun startNewRound() {
        viewModelScope.launch {  // Make startNewRound use coroutines
            val currentState = _gameState.value ?: return@launch
            val lastResult = currentState.roundHistory.lastOrNull() ?: return@launch
            
            val nextDealer = roundTracker.getNextDealer(
                lastResult,
                currentState.currentDealer
            )
            
            // Create new players list with same names but fresh hands
            val players = listOf(
                Player(
                    id = 0,
                    name = currentState.players[0].name,
                    isHuman = true,
                    team = 0,
                    _hand = mutableListOf()
                ),
                Player(
                    id = 1,
                    name = "Player 2",
                    isHuman = false,
                    team = 1,
                    _hand = mutableListOf()
                ),
                Player(
                    id = 2,
                    name = "Player 3",
                    isHuman = false,
                    team = 0,
                    _hand = mutableListOf()
                ),
                Player(
                    id = 3,
                    name = "Player 4",
                    isHuman = false,
                    team = 1,
                    _hand = mutableListOf()
                )
            )

            // Initialize new game state
            _gameState.value = GameState(
                players = players,
                currentDealer = nextDealer,
                currentPlayer = (nextDealer + 1) % 4,
                gamePhase = GamePhase.TRUMP_SELECTION
            )

            // Deal cards
            gameRepository.dealCards(players)
            
            // Add delay before bot selects trump
            delay(2000)  // Give players time to see their new hands
            
            // If the player to dealer's right is a bot, have them select trump
            val rightOfDealer = (nextDealer + 1) % 4
            if (!players[rightOfDealer].isHuman) {
                val botHand = players[rightOfDealer].hand
                val trumpIndex = botHand.indexOfFirst { !it.isTen }
                selectTrump(trumpIndex)
                // Remove handleBotMoves() call from here as it's already called in selectTrump if needed
            }
        }
    }

    fun revealTrump() {
        val currentState = _gameState.value ?: return
        
        Log.d("GameSetupViewModel", "=== Revealing Trump ===")
        Log.d("GameSetupViewModel", "Current phase: ${currentState.gamePhase}")
        
        val newState = trickPlayingRepository.revealTrump(currentState).copy(
            gamePhase = GamePhase.PLAYING
        )
        
        Log.d("GameSetupViewModel", "After revealing trump:")
        Log.d("GameSetupViewModel", "- Trump revealed: ${newState.trumpRevealed}")
        Log.d("GameSetupViewModel", "- Game phase: ${newState.gamePhase}")
        
        _gameState.value = newState
    }

    fun canPlayCard(playerIndex: Int, card: Card): Boolean {
        return _gameState.value?.let { state ->
            trickPlayingRepository.canPlayCard(state, playerIndex, card)
        } ?: false
    }
} 