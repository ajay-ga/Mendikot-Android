package com.mendikotapp.viewmodel

import androidx.lifecycle.ViewModel
import com.mendikotapp.data.models.GameState
import com.mendikotapp.data.models.Player
import com.mendikotapp.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GameSetupViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()
    
    fun setupGame(humanPlayerName: String) {
        // Create players
        val firstDealer = Random.nextInt(4)
        val players = listOf(
            Player(0, humanPlayerName, true, 0),
            Player(1, "Bot 1", false, 1),
            Player(2, "Bot 2", false, 0),
            Player(3, "Bot 3", false, 1)
        )
        
        // Initialize game state
        _gameState.value = GameState(
            players = players,
            currentDealer = firstDealer
        )
        
        // Deal cards
        gameRepository.dealCards(players)
    }
    
    fun selectTrump(cardIndex: Int) {
        val currentState = _gameState.value ?: return
        val rightOfDealer = (currentState.currentDealer + 1) % 4
        val trumpSelector = currentState.players[rightOfDealer]
        
        val trumpCard = gameRepository.selectTrumpCard(trumpSelector, cardIndex)
        
        _gameState.value = currentState.copy(
            trumpCard = trumpCard
        )
    }
} 