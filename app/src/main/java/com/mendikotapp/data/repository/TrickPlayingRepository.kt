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
        Log.d("TrickPlaying", "=== Playing Card ===")
        Log.d("TrickPlaying", "Player $playerIndex playing card at index $cardIndex")
        Log.d("TrickPlaying", "Before play - Hand size: ${state.players[playerIndex].hand.size}")
        
        val player = state.players[playerIndex]
        val card = player.hand[cardIndex]
        
        // Create new hand without the played card
        val newHand = player.hand.toMutableList()
        newHand.removeAt(cardIndex)
        
        // Create new player with updated hand
        val updatedPlayer = Player(
            id = player.id,
            name = player.name,
            isHuman = player.isHuman,
            team = player.team,
            _hand = newHand
        )
        
        // Update players list
        val newPlayers = state.players.toMutableList()
        newPlayers[playerIndex] = updatedPlayer
        
        // Add card to current trick
        val newTrick = state.currentTrick + (playerIndex to card)
        
        // Set led suit if this is the first card of the trick
        val newLedSuit = if (state.currentTrick.isEmpty()) card.suit else state.ledSuit
        
        Log.d("TrickPlaying", "After play - Hand size: ${newHand.size}")
        Log.d("TrickPlaying", "Current trick size: ${newTrick.size}")
        Log.d("TrickPlaying", "Led suit: $newLedSuit")

        // If trick is complete, determine winner and update scores
        if (newTrick.size == 4) {
            val (winnerIndex, _) = determineTrickWinner(newTrick, state.trumpCard?.suit)
            Log.d("TrickPlaying", "Trick completed - Winner: Player $winnerIndex")
            
            // Count tens in the trick
            val tensInTrick = newTrick.count { it.second.isTen }
            
            // Update scores based on winner's team
            val winningTeam = winnerIndex % 2
            val (newTeam1Score, newTeam2Score) = if (winningTeam == 0) {
                Pair(state.team1Score + 1, state.team2Score)
            } else {
                Pair(state.team1Score, state.team2Score + 1)
            }
            
            // Update tens count based on winner's team
            val (newTeam1Tens, newTeam2Tens) = if (winningTeam == 0) {
                Pair(state.team1Tens + tensInTrick, state.team2Tens)
            } else {
                Pair(state.team1Tens, state.team2Tens + tensInTrick)
            }
            
            Log.d("TrickPlaying", "Team 1 Score: $newTeam1Score, Tens: $newTeam1Tens")
            Log.d("TrickPlaying", "Team 2 Score: $newTeam2Score, Tens: $newTeam2Tens")
            
            return state.copy(
                players = newPlayers,
                currentTrick = newTrick,
                currentPlayer = winnerIndex,  // Winner leads next trick
                ledSuit = newLedSuit,
                team1Score = newTeam1Score,
                team2Score = newTeam2Score,
                team1Tens = newTeam1Tens,
                team2Tens = newTeam2Tens
            )
        }
        
        return state.copy(
            players = newPlayers,
            currentTrick = newTrick,
            currentPlayer = (playerIndex + 1) % 4,
            ledSuit = newLedSuit
        )
    }
    
    fun revealTrump(gameState: GameState): GameState {
        val trumpCard = gameState.trumpCard ?: return gameState
        val trumpOwnerIndex = (gameState.currentDealer + 1) % 4
        val trumpOwner = gameState.players[trumpOwnerIndex]
        
        trumpOwner.addCard(trumpCard)
        
        return gameState.copy(trumpRevealed = true)
    }
} 