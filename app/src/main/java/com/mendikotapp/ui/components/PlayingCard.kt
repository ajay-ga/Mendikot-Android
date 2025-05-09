package com.mendikotapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mendikotapp.data.models.Card
import com.mendikotapp.data.models.Suit

@Composable
fun PlayingCard(
    card: Card,
    modifier: Modifier = Modifier,
    faceDown: Boolean = false,
    isTrumpSuit: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val clickableModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }
    
    Box(
        modifier = clickableModifier
            .size(80.dp, 120.dp)
            .background(
                color = if (isTrumpSuit) 
                    MaterialTheme.colorScheme.primaryContainer 
                else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = if (isTrumpSuit) 2.dp else 1.dp,
                color = if (isTrumpSuit) 
                    MaterialTheme.colorScheme.primary 
                else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        if (!faceDown) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = when (card.value) {
                        14 -> "A"
                        13 -> "K"
                        12 -> "Q"
                        11 -> "J"
                        else -> card.value.toString()
                    },
                    color = if (card.suit in listOf(Suit.HEARTS, Suit.DIAMONDS)) 
                        Color.Red else Color.Black,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = when(card.suit) {
                        Suit.HEARTS -> "♥"
                        Suit.DIAMONDS -> "♦"
                        Suit.CLUBS -> "♣"
                        Suit.SPADES -> "♠"
                    },
                    color = if (card.suit in listOf(Suit.HEARTS, Suit.DIAMONDS)) 
                        Color.Red else Color.Black,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(start = 4.dp)
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