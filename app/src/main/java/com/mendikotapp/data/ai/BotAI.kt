package com.mendikotapp.data.ai

import com.mendikotapp.data.models.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay

@Singleton
class BotAI @Inject constructor(
    private val logger: BotDecisionLogger
) {
    companion object {
        private const val BOT_THINKING_TIME = 1000L // 1 second
        private const val BOT_PLAYING_TIME = 500L   // 0.5 seconds
    }
    
    suspend fun selectCardToPlay(
        gameState: GameState,
        botIndex: Int
    ): Int {
        val bot = gameState.players[botIndex]
        val ledSuit = gameState.ledSuit
        val trumpSuit = gameState.trumpCard?.suit

        // Simulate bot thinking
        delay(BOT_THINKING_TIME)

        // If we're leading
        if (ledSuit == null) {
            val cardIndex = selectLeadCard(bot.hand, trumpSuit)
            delay(BOT_PLAYING_TIME)
            return cardIndex
        }

        // Must follow suit if possible
        val followingSuit = bot.hand.filter { it.suit == ledSuit }
        if (followingSuit.isNotEmpty()) {
            val selectedCard = followingSuit.first()
            val cardIndex = bot.hand.indexOf(selectedCard)
            logger.logCardPlay(
                gameState,
                botIndex,
                selectedCard,
                "Following suit"
            )
            delay(BOT_PLAYING_TIME)
            return cardIndex
        }

        // Must play trump if we have it and it's revealed
        if (gameState.trumpRevealed) {
            val trumpCards = bot.hand.filter { it.suit == trumpSuit }
            if (trumpCards.isNotEmpty()) {
                val selectedCard = trumpCards.first()
                val cardIndex = bot.hand.indexOf(selectedCard)
                logger.logCardPlay(
                    gameState,
                    botIndex,
                    selectedCard,
                    "Playing trump"
                )
                delay(BOT_PLAYING_TIME)
                return cardIndex
            }
        }

        // Play our lowest card
        val cardIndex = findLowestValueCardIndex(bot.hand)
        logger.logCardPlay(
            gameState,
            botIndex,
            bot.hand[cardIndex],
            "Playing lowest card"
        )
        delay(BOT_PLAYING_TIME)
        return cardIndex
    }

    private fun selectLeadCard(
        hand: List<Card>,
        trumpSuit: Suit?
    ): Int {
        // Prefer leading with high non-trump cards
        val nonTrumpCards = hand.filter { it.suit != trumpSuit }
        if (nonTrumpCards.isNotEmpty()) {
            // Find highest value non-trump card
            val highestCard = nonTrumpCards.maxByOrNull { it.value }
            return hand.indexOf(highestCard)
        }
        
        // If we only have trump cards, play lowest
        return findLowestValueCardIndex(hand)
    }

    private fun findLowestValueCardIndex(hand: List<Card>): Int {
        val lowestCard = hand.minByOrNull { it.value } ?: return 0
        return hand.indexOf(lowestCard)
    }

    private fun evaluateHand(hand: List<Card>, trumpSuit: Suit?): Int {
        return hand.sumOf { card ->
            when {
                card.suit == trumpSuit -> card.value * 2  // Trump cards worth double
                card.isTen -> 20  // Tens are valuable
                card.value >= 11 -> 15  // Face cards and Aces
                else -> card.value
            }
        }
    }

    suspend fun shouldRevealTrump(gameState: GameState, botIndex: Int): Boolean {
        delay(BOT_THINKING_TIME)
        val bot = gameState.players[botIndex]
        val trumpSuit = gameState.trumpCard?.suit
        
        val trumpCount = bot.hand.count { it.suit == trumpSuit }
        val handValue = evaluateHand(bot.hand, trumpSuit)
        
        return trumpCount >= 3 && handValue >= 30
    }
} 