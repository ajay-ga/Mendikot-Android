package com.mendikotapp.data.ai

import android.util.Log
import com.mendikotapp.data.models.Card
import com.mendikotapp.data.models.GameState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BotDecisionLogger @Inject constructor() {
    private val TAG = "BotAI"

    fun logCardPlay(
        gameState: GameState,
        botIndex: Int,
        card: Card,
        reason: String
    ) {
        val bot = gameState.players[botIndex]
        Log.d(TAG, """
            Bot ${bot.name} played ${card.rank} of ${card.suit}
            Reason: $reason
            Current trick: ${gameState.currentTrick}
            Led suit: ${gameState.ledSuit}
            Trump revealed: ${gameState.trumpRevealed}
            Trump suit: ${gameState.trumpCard?.suit}
            Hand: ${bot.hand}
        """.trimIndent())
    }

    fun logTrumpReveal(
        gameState: GameState,
        botIndex: Int,
        reason: String
    ) {
        val bot = gameState.players[botIndex]
        Log.d(TAG, """
            Bot ${bot.name} revealed trump
            Reason: $reason
            Trump card: ${gameState.trumpCard}
            Hand: ${bot.hand}
        """.trimIndent())
    }
} 