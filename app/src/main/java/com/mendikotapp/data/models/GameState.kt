package com.mendikotapp.data.models

class Player(
    val id: Int,
    val name: String,
    val isHuman: Boolean,
    val team: Int, // 0 or 1
    private val _hand: MutableList<Card> = mutableListOf()
) {
    val hand: MutableList<Card> get() = _hand

    fun addCard(card: Card) {
        _hand.add(card)
    }

    fun removeCard(index: Int): Card {
        return _hand.removeAt(index)
    }

    fun clearHand() {
        _hand.clear()
    }

    // Instead of copy, create a new instance with current values
    fun duplicate(
        newId: Int = id,
        newName: String = name,
        newIsHuman: Boolean = isHuman,
        newTeam: Int = team,
        newHand: List<Card> = hand
    ): Player {
        return Player(
            id = newId,
            name = newName,
            isHuman = newIsHuman,
            team = newTeam,
            _hand = newHand.toMutableList()
        )
    }
}

enum class GamePhase {
    TRUMP_SELECTION,
    PLAYING,
    COMPLETED
}

data class GameState(
    val players: List<Player>,
    val currentDealer: Int,
    val currentPlayer: Int,
    val trumpCard: Card? = null,
    val trumpRevealed: Boolean = false,
    val currentTrick: List<Pair<Int, Card>> = emptyList(),
    val completedTricks: List<List<Pair<Int, Card>>> = emptyList(),
    val ledSuit: Suit? = null,
    val team1Score: Int = 0,
    val team2Score: Int = 0,
    val team1Tens: Int = 0,
    val team2Tens: Int = 0,
    val roundState: RoundState = RoundState.IN_PROGRESS,
    val roundHistory: List<RoundResult> = emptyList(),
    val gamePhase: GamePhase = GamePhase.TRUMP_SELECTION
)

enum class RoundState {
    IN_PROGRESS,
    COMPLETED
} 