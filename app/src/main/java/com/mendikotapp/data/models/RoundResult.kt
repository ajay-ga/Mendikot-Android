package com.mendikotapp.data.models

data class RoundResult(
    val winningTeam: Int, // 0 or 1
    val team1Tricks: Int,
    val team2Tricks: Int,
    val team1Tens: Int,
    val team2Tens: Int,
    val winType: WinType
)

enum class WinType {
    REGULAR,    // Normal win (7+ tricks with 2-2 tens, or 3+ tens)
    MENDIKOT,   // All 4 tens
    WHITEWASH   // All 13 tricks
} 