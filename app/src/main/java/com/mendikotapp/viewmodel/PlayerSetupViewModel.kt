package com.mendikotapp.viewmodel

import androidx.lifecycle.ViewModel
import com.mendikotapp.data.models.GamePhase
import com.mendikotapp.data.models.GameState
import com.mendikotapp.data.models.Player
import com.mendikotapp.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.random.Random

data class PlayerSetupState(
    val humanPlayerName: String = "",
    val botNames: List<String> = listOf("Bot 1", "Bot 2", "Bot 3"),
    val isValidName: Boolean = false
)

@HiltViewModel
class PlayerSetupViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _state = MutableStateFlow(PlayerSetupState())
    val state: StateFlow<PlayerSetupState> = _state.asStateFlow()

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    fun updatePlayerName(name: String) {
        _state.value = _state.value.copy(
            humanPlayerName = name,
            isValidName = name.trim().length >= 2
        )
    }

    fun setupPlayers(humanPlayerName: String) {
        // Create initial game state with random dealer
        val dealer = Random.nextInt(1, 4)
        
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

        _gameState.value = GameState(
            players = players,
            currentDealer = dealer,
            currentPlayer = (dealer + 1) % 4,  // Set current player to right of dealer
            gamePhase = GamePhase.TRUMP_SELECTION
        )
    }
} 