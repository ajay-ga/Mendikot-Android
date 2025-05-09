package com.mendikotapp.data.scoring

import com.mendikotapp.data.models.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoundTracker @Inject constructor() {
    
    fun isRoundComplete(gameState: GameState): Boolean {
        return gameState.completedTricks.size == 13
    }
    
    fun determineRoundResult(gameState: GameState): RoundResult {
        val team1Tricks = gameState.team1Score
        val team2Tricks = gameState.team2Score
        val team1Tens = gameState.team1Tens
        val team2Tens = gameState.team2Tens
        
        // Check for Whitewash (all 13 tricks)
        if (team1Tricks == 13) {
            return RoundResult(
                winningTeam = 0,
                team1Tricks = team1Tricks,
                team2Tricks = team2Tricks,
                team1Tens = team1Tens,
                team2Tens = team2Tens,
                winType = WinType.WHITEWASH
            )
        }
        if (team2Tricks == 13) {
            return RoundResult(
                winningTeam = 1,
                team1Tricks = team1Tricks,
                team2Tricks = team2Tricks,
                team1Tens = team1Tens,
                team2Tens = team2Tens,
                winType = WinType.WHITEWASH
            )
        }
        
        // Check for Mendikot (all 4 tens)
        if (team1Tens == 4) {
            return RoundResult(
                winningTeam = 0,
                team1Tricks = team1Tricks,
                team2Tricks = team2Tricks,
                team1Tens = team1Tens,
                team2Tens = team2Tens,
                winType = WinType.MENDIKOT
            )
        }
        if (team2Tens == 4) {
            return RoundResult(
                winningTeam = 1,
                team1Tricks = team1Tricks,
                team2Tricks = team2Tricks,
                team1Tens = team1Tens,
                team2Tens = team2Tens,
                winType = WinType.MENDIKOT
            )
        }
        
        // Regular win conditions
        val winningTeam = when {
            // If one team has 3 or more tens, they win
            team1Tens >= 3 -> 0
            team2Tens >= 3 -> 1
            
            // If tens are split 2-2, team with 7+ tricks wins
            team1Tens == 2 && team2Tens == 2 -> {
                if (team1Tricks >= 7) 0 else 1
            }
            
            // Shouldn't happen in a valid game
            else -> -1
        }
        
        return RoundResult(
            winningTeam = winningTeam,
            team1Tricks = team1Tricks,
            team2Tricks = team2Tricks,
            team1Tens = team1Tens,
            team2Tens = team2Tens,
            winType = WinType.REGULAR
        )
    }
    
    fun getNextDealer(currentResult: RoundResult, currentDealer: Int): Int {
        // Winner's team member to the right of current dealer becomes next dealer
        val dealerTeam = currentDealer % 2
        // FIXME: Fix this logic as if dealer is bot3, the trump card is not being selected
        return if (currentResult.winningTeam == dealerTeam) {
            val cd = (currentDealer + 1) % 4
            if(cd == 3) {
                currentDealer
            } else
            cd
        } else {
            val cd = (currentDealer + 3) % 4
            if(cd == 3) {
                currentDealer
            } else
                cd
        }
    }
} 