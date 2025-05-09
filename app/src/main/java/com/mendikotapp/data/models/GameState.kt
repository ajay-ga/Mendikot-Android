package com.mendikotapp.data.models

data class Player(
    val id: Int,
    val name: String,
    val isHuman: Boolean,
    val team: Int, // 0 or 1
    val hand: MutableList<Card> = mutableListOf()
)

data class GameState(
    val players: List<Player>,
    val currentDealer: Int = 0,
    val trumpCard: Card? = null,
    val trumpRevealed: Boolean = false,
    val currentTrick: List<Card> = emptyList(),
    val trickLeader: Int = (currentDealer + 1) % 4,
    val currentPlayer: Int = trickLeader,
    val team1Score: Int = 0,
    val team2Score: Int = 0,
    val team1Tens: Int = 0,
    val team2Tens: Int = 0
) 