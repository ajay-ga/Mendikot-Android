package com.mendikotapp.data.repository

import com.mendikotapp.data.models.*
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

@Singleton
class TrickPlayingRepository @Inject constructor() {
    
    fun canPlayCard(gameState: GameState, playerIndex: Int, card: Card): Boolean {
        // Can't play if it's not your turn
        if (playerIndex != gameState.currentPlayer) return false
        
        val player = gameState.players[playerIndex]
        val ledSuit = gameState.ledSuit
        
        // First player can play any card
        if (ledSuit == null) return true
        
        // Must follow suit if possible
        val hasSuit = player.hand.any { it.suit == ledSuit }
        if (hasSuit) {
            return card.suit == ledSuit
        }
        
        // If trump is revealed and player has trump, must play trump
        if (gameState.trumpRevealed && 
            player.hand.any { it.suit == gameState.trumpCard?.suit }) {
            return card.suit == gameState.trumpCard?.suit
        }
        
        // Can play any card if can't follow suit
        return true
    }
    
    private fun determineTrickWinner(
        trick: List<Pair<Int, Card>>,
        trumpSuit: Suit?
    ): Pair<Int, Card> {
        if (trick.isEmpty()) throw IllegalStateException("Cannot determine winner of empty trick")
        
        // Get the led suit from the first card played
        val ledSuit = trick.first().second.suit
        
        // Find winning card
        return trick.maxByOrNull { (_, card) ->
            when {
                // Trump cards beat everything
                card.suit == trumpSuit -> 1000 + card.value
                // Following suit cards ranked by value
                card.suit == ledSuit -> card.value
                // Other cards are worth nothing
                else -> 0
            }
        } ?: trick.first()
    }
    
    fun playCard(state: GameState, playerIndex: Int, cardIndex: Int): GameState {
        if (state.gamePhase != GamePhase.PLAYING) {
            Log.e("TrickPlayingRepository", "Cannot play card - wrong game phase: ${state.gamePhase}")
            return state
        }
        
        val player = state.players[playerIndex]
        val card = player.removeCard(cardIndex)
        
        Log.d("TrickPlayingRepository", "\n=== Card Played ===")
        Log.d("TrickPlayingRepository", "${player.name} played: $card")
        
        val newTrick = state.currentTrick + (playerIndex to card)
        val newLedSuit = if (state.currentTrick.isEmpty()) card.suit else state.ledSuit
        val nextPlayer = (playerIndex + 1) % 4
        
        // If trick is complete (4 cards), determine winner
        if (newTrick.size == 4) {
            val (winnerIndex, winningCard) = determineTrickWinner(newTrick, state.trumpCard?.suit)
            Log.d("TrickPlayingRepository", "\n=== Trick Complete ===")
            Log.d("TrickPlayingRepository", "Trick: ${newTrick.joinToString { "${state.players[it.first].name}: ${it.second}" }}")
            Log.d("TrickPlayingRepository", "Winner: ${state.players[winnerIndex].name} with $winningCard")
            Log.d("TrickPlayingRepository", "Next player to lead: ${state.players[winnerIndex].name}")
            
            return GameState(
                players = state.players,
                currentDealer = state.currentDealer,
                currentPlayer = winnerIndex,  // Winner leads next trick
                trumpCard = state.trumpCard,
                trumpRevealed = state.trumpRevealed,
                currentTrick = emptyList(),  // Clear trick
                ledSuit = null,  // Reset led suit for next trick
                completedTricks = state.completedTricks + listOf(newTrick),  // Wrap newTrick in listOf()
                team1Score = state.team1Score + if (winnerIndex % 2 == 0) 1 else 0,
                team2Score = state.team2Score + if (winnerIndex % 2 == 1) 1 else 0,
                team1Tens = state.team1Tens + if (winnerIndex % 2 == 0) newTrick.count { it.second.isTen } else 0,
                team2Tens = state.team2Tens + if (winnerIndex % 2 == 1) newTrick.count { it.second.isTen } else 0,
                gamePhase = state.gamePhase,
                roundState = state.roundState,
                roundHistory = state.roundHistory
            )
        } else {
            Log.d("TrickPlayingRepository", "Next player: ${state.players[nextPlayer].name}")
            return state.copy(
                currentTrick = newTrick,
                ledSuit = newLedSuit,
                currentPlayer = nextPlayer
            )
        }
    }
    
    fun revealTrump(gameState: GameState): GameState {
        val trumpCard = gameState.trumpCard ?: return gameState
        val trumpOwnerIndex = (gameState.currentDealer + 1) % 4
        val trumpOwner = gameState.players[trumpOwnerIndex]
        
        trumpOwner.addCard(trumpCard)
        
        return gameState.copy(trumpRevealed = true)
    }
} 