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
    
    private val viewModelScope = CoroutineScope(Dispatchers.Main + Job())
    
    init {
        // Log when ViewModel is created
        Log.d("GameSetupViewModel", "ViewModel initialized")
    }
    
    fun initializeGame(humanPlayerName: String) {
        Log.d("GameSetupViewModel", "Initializing game with player: $humanPlayerName")
        
        // Create players
        val players = listOf(
            Player(
                id = 0,
                name = humanPlayerName,
                isHuman = true,
                team = 0,
                _hand = mutableListOf()
            ),
            Player(
                id = 1,
                name = "Bot 1",
                isHuman = false,
                team = 1,
                _hand = mutableListOf()
            ),
            Player(
                id = 2,
                name = "Bot 2",
                isHuman = false,
                team = 0,
                _hand = mutableListOf()
            ),
            Player(
                id = 3,
                name = "Bot 3",
                isHuman = false,
                team = 1,
                _hand = mutableListOf()
            )
        )

        // Initialize game state with random dealer
        // FIXME: Fix this logic as if dealer is bot3, the trump card is not being selected
        var dealer = Random.nextInt(4)
        if(dealer == 3) {
            dealer = 2
        }
        Log.d("GameSetupViewModel", "Selected dealer: $dealer")
        
        val initialState = GameState(
            players = players,
            currentDealer = dealer,
            currentPlayer = (dealer + 1) % 4,  // Set current player to right of dealer
            gamePhase = GamePhase.TRUMP_SELECTION
        )
        _gameState.value = initialState
        Log.d("GameSetupViewModel", "Initial state set: $initialState")

        // Deal cards
        gameRepository.dealCards(players)
        Log.d("GameSetupViewModel", "Cards dealt. Player 0 hand size: ${players[0].hand.size}")
        
        // Update state after dealing
        _gameState.value = _gameState.value?.copy(
            players = players
        )
        
        // If the player to dealer's right is a bot, have them select trump immediately
        val rightOfDealer = (dealer + 1) % 4
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
            
            _gameState.value = newState
            
            // Add longer delay after trump selection to show the selected card
            delay(3000)
            
            // Start bot moves if next player is a bot
            if (!newState.players[newState.currentPlayer].isHuman) {
                handleBotMoves()
            }
        }
    }
    
    fun playCard(playerIndex: Int, cardIndex: Int) {
        viewModelScope.launch {  // Make playCard a suspend function
            val currentState = _gameState.value ?: run {
                Log.e("GameSetupViewModel", "Current state is null during play card")
                return@launch
            }
            
            Log.d("GameSetupViewModel", "=== Playing Card ===")
            Log.d("GameSetupViewModel", "Player: ${currentState.players[playerIndex].name}")
            Log.d("GameSetupViewModel", "Card index: $cardIndex")
            Log.d("GameSetupViewModel", "Game phase: ${currentState.gamePhase}")
            
            // Check if we're in the playing phase
            if (currentState.gamePhase != GamePhase.PLAYING) {
                Log.e("GameSetupViewModel", "Cannot play card - wrong game phase: ${currentState.gamePhase}")
                return@launch
            }
            
            // Verify it's the player's turn
            if (currentState.currentPlayer != playerIndex) {
                Log.e("GameSetupViewModel", "Not player's turn. Current player: ${currentState.currentPlayer}")
                return@launch
            }
            
            // For human player
            if (currentState.players[playerIndex].isHuman) {
                val selectedCard = currentState.players[playerIndex].hand[cardIndex]
                Log.d("GameSetupViewModel", "Human selected card: $selectedCard")
                
                if (!trickPlayingRepository.canPlayCard(currentState, playerIndex, selectedCard)) {
                    Log.e("GameSetupViewModel", "Invalid card play")
                    return@launch
                }
                
                // Play the card and update state
                val newState = trickPlayingRepository.playCard(currentState, playerIndex, cardIndex)
                _gameState.value = newState
                
                // If trick is complete, add delay before next action
                if (newState.currentTrick.size == 4) {
                    delay(2000) // 2 second delay to show completed trick
                }
                
                // Trigger bot moves after human move if next player is a bot
                if (!newState.players[newState.currentPlayer].isHuman) {
                    handleBotMoves()
                }
            }
        }
    }
    
    private fun handleBotMoves() {
        viewModelScope.launch {
            var state = _gameState.value ?: return@launch
            
            // Continue while current player is a bot
            while (!state.players[state.currentPlayer].isHuman && 
                   state.currentTrick.size < 4) {
                
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
                
                // If trick is complete, add extra delay
                if (state.currentTrick.size == 4) {
                    delay(2000) // 2 seconds delay for completed trick
                }
                
                // Check if round is complete
                if (roundTracker.isRoundComplete(state)) {
                    Log.d("GameSetupViewModel", "Round completed")
                    val result = roundTracker.determineRoundResult(state)
                    state = state.copy(
                        roundState = RoundState.COMPLETED,
                        roundHistory = state.roundHistory + result
                    )
                    _gameState.value = state
                    delay(2000) // Extra delay at round end
                    break
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
                    name = "Bot 1",
                    isHuman = false,
                    team = 1,
                    _hand = mutableListOf()
                ),
                Player(
                    id = 2,
                    name = "Bot 2",
                    isHuman = false,
                    team = 0,
                    _hand = mutableListOf()
                ),
                Player(
                    id = 3,
                    name = "Bot 3",
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
} 