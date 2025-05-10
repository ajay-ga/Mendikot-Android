package com.mendikotapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.mendikotapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.cards_splash)
    )
    
    val lottieAnimation by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
    )

    LaunchedEffect(lottieAnimation) {
        // Wait for animation to finish or timeout after 2 seconds
        delay(2000)
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { lottieAnimation }
        )
    }
} 