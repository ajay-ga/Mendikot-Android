package com.mendikotapp.data.repository

import com.mendikotapp.data.models.*
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

@Singleton
class GameRepository @Inject constructor() {
    private val deck = mutableListOf<Card>()
    
    init {
        createDeck()
    }
    
    private fun createDeck() {
        deck.clear()
        // Create all 52 cards (4 suits × 13 ranks)
        Suit.values().forEach { suit ->
            // Add number cards (2-10)
            (2..10).forEach { value ->
                deck.add(Card(suit = suit, value = value))
            }
            
            // Add face cards and Ace
            deck.add(Card(suit = suit, value = 11))  // Jack
            deck.add(Card(suit = suit, value = 12))  // Queen
            deck.add(Card(suit = suit, value = 13))  // King
            deck.add(Card(suit = suit, value = 14))  // Ace
        }
        deck.shuffle()
    }
    
    fun dealCards(players: List<Player>) {
        // Reset deck and hands
        createDeck()
        players.forEach { it.clearHand() }
        
        Log.d("GameRepository", "=== Starting Card Distribution ===")
        
        // First round: Deal 5 cards to each player
        repeat(5) { round ->
            Log.d("GameRepository", "-- Round ${round + 1} (5 cards) --")
            players.forEach { player ->
                deck.removeFirstOrNull()?.let { card ->
                    player.addCard(card)
                    Log.d("GameRepository", "${player.name} received: $card")
                }
            }
        }
        
        // Second and third rounds: Deal 4 cards to each player
        repeat(2) { round ->
            Log.d("GameRepository", "-- Round ${round + 6} (4 cards) --")
            players.forEach { player ->
                repeat(4) {
                    deck.removeFirstOrNull()?.let { card ->
                        player.addCard(card)
                        Log.d("GameRepository", "${player.name} received: $card")
                    }
                }
            }
        }
        
        // Log final hands
        Log.d("GameRepository", "\n=== Final Hands ===")
        players.forEach { player ->
            Log.d("GameRepository", "${player.name}'s hand (${player.hand.size} cards): ${player.hand.joinToString()}")
        }
    }
    
    fun selectTrumpCard(player: Player, cardIndex: Int): Card {
        Log.d("GameRepository", "=== Selecting Trump Card ===")
        Log.d("GameRepository", "Player: ${player.name}")
        Log.d("GameRepository", "Card index: $cardIndex")
        Log.d("GameRepository", "Player's hand: ${player.hand.joinToString()}")
        
        // Remove and return the selected card
        val selectedCard = player.removeCard(cardIndex)
        Log.d("GameRepository", "Selected card: $selectedCard")
        Log.d("GameRepository", "Player's hand after selection: ${player.hand.joinToString()}")
        
        return selectedCard
    }
    
    fun revealTrump(gameState: GameState) {
        gameState.trumpCard?.let { trump ->
            val trumpOwner = gameState.players[(gameState.currentDealer + 1) % 4]
            trumpOwner.hand.add(trump)
        }
    }
} 