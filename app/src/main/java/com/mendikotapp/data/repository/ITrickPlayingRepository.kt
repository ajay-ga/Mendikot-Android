package com.mendikotapp.data.repository

import com.mendikotapp.data.models.*

interface ITrickPlayingRepository {
    fun canPlayCard(gameState: GameState, playerIndex: Int, card: Card): Boolean
    fun playCard(gameState: GameState, playerIndex: Int, cardIndex: Int): GameState
    fun revealTrump(gameState: GameState): GameState
} 