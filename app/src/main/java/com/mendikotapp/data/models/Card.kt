package com.mendikotapp.data.models

enum class Suit {
    HEARTS, DIAMONDS, CLUBS, SPADES
}

enum class Rank(val value: Int) {
    ACE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(11),
    QUEEN(12),
    KING(13)
}

data class Card(
    val suit: Suit,
    val rank: Rank,
    val isTrump: Boolean = false
) {
    val isTen: Boolean get() = rank == Rank.TEN
    
    fun isHigherThan(other: Card, ledSuit: Suit): Boolean {
        return when {
            this.isTrump && !other.isTrump -> true
            !this.isTrump && other.isTrump -> false
            this.isTrump && other.isTrump -> this.rank.value > other.rank.value
            this.suit == ledSuit && other.suit != ledSuit -> true
            this.suit != ledSuit && other.suit == ledSuit -> false
            else -> this.rank.value > other.rank.value
        }
    }
} 