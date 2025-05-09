package com.mendikotapp.data.repository

import com.mendikotapp.data.models.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor() {
    private val deck = mutableListOf<Card>()
    
    init {
        createDeck()
    }
    
    private fun createDeck() {
        deck.clear()
        Suit.values().forEach { suit ->
            Rank.values().forEach { rank ->
                deck.add(Card(suit, rank))
            }
        }
        deck.shuffle()
    }
    
    fun dealCards(players: List<Player>) {
        // Deal 5 cards to each player
        repeat(5) { round ->
            players.forEach { player ->
                deck.removeFirstOrNull()?.let { card ->
                    player.hand.add(card)
                }
            }
        }
        
        // Deal remaining 8 cards (4 each)
        repeat(2) { round ->
            players.forEach { player ->
                repeat(4) {
                    deck.removeFirstOrNull()?.let { card ->
                        player.hand.add(card)
                    }
                }
            }
        }
    }
    
    fun selectTrumpCard(player: Player, cardIndex: Int): Card {
        val trumpCard = player.hand.removeAt(cardIndex)
        return trumpCard.copy(isTrump = true)
    }
    
    fun revealTrump(gameState: GameState) {
        gameState.trumpCard?.let { trump ->
            val trumpOwner = gameState.players[(gameState.currentDealer + 1) % 4]
            trumpOwner.hand.add(trump)
        }
    }
} 