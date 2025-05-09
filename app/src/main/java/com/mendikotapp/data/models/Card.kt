package com.mendikotapp.data.models

enum class Suit(val symbol: String) {
    HEARTS("♥"),
    DIAMONDS("♦"),
    CLUBS("♣"),
    SPADES("♠")
}

data class Card(
    val suit: Suit,
    val value: Int  // 2-10 = 2-10, J=11, Q=12, K=13, A=14
) {
    val isTen: Boolean get() = value == 10
    
    val rank: String get() = when (value) {
        14 -> "A"
        13 -> "K"
        12 -> "Q"
        11 -> "J"
        else -> value.toString()
    }
    
    override fun toString(): String = "$rank${suit.symbol}"
    
    fun isHigherThan(other: Card, ledSuit: Suit, trumpSuit: Suit? = null): Boolean {
        return when {
            // Trump cards beat everything
            this.suit == trumpSuit && other.suit != trumpSuit -> true
            other.suit == trumpSuit && this.suit != trumpSuit -> false
            
            // If both cards are trump, higher value wins
            this.suit == trumpSuit && other.suit == trumpSuit -> this.value > other.value
            
            // Following suit beats non-following suit
            this.suit == ledSuit && other.suit != ledSuit -> true
            other.suit == ledSuit && this.suit != ledSuit -> false
            
            // If both cards follow suit or both don't, higher value wins
            else -> this.value > other.value
        }
    }
} 