package com.mendikotapp.data.repository

import com.mendikotapp.data.models.*

interface IGameRepository {
    fun createDeck()
    fun dealCards(players: List<Player>)
    fun selectTrumpCard(player: Player, cardIndex: Int): Card
} 