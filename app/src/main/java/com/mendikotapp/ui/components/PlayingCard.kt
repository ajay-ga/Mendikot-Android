package com.mendikotapp.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mendikotapp.data.models.Card
import com.mendikotapp.data.models.Suit

@Composable
fun PlayingCard(
    card: Card,
    modifier: Modifier = Modifier,
    faceDown: Boolean = false
) {
    Card(
        modifier = modifier
            .size(width = 60.dp, height = 90.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        if (!faceDown) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = card.rank.toString(),
                    color = if (card.suit in listOf(Suit.HEARTS, Suit.DIAMONDS)) 
                        Color.Red else Color.Black,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = when(card.suit) {
                        Suit.HEARTS -> "♥"
                        Suit.DIAMONDS -> "♦"
                        Suit.CLUBS -> "♣"
                        Suit.SPADES -> "♠"
                    },
                    color = if (card.suit in listOf(Suit.HEARTS, Suit.DIAMONDS)) 
                        Color.Red else Color.Black,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎴",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
    }
} 