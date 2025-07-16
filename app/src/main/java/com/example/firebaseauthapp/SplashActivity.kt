package com.example.firebaseauthapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.alpha
import com.example.firebaseauthapp.ui.theme.FirebaseAuthAppTheme
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseAuthAppTheme {
                GlitchSplashScreen(
                    onAnimationFinished = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun GlitchSplashScreen(onAnimationFinished: () -> Unit) {
    val text = "VSPRIME"
    val glitchColors = listOf(Color.White, Color.Cyan, Color(0xFFFFFFFF), Color(0xFF830000))
    var displayText by remember { mutableStateOf(text) }
    var color by remember { mutableStateOf(Color.White) }
    var alpha by remember { mutableStateOf(1f) }

    LaunchedEffect(Unit) {
        repeat(30) {
            displayText = text.map { c ->
                if ((0..4).random() == 0) ('A'..'Z').random() else c
            }.joinToString("")
            color = glitchColors.random()
            delay(60)
        }
        displayText = text
        color = Color.White
        delay(500)
        alpha = 0f
        delay(400)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101018)),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = displayText,
            color = color,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 8.sp,
            modifier = Modifier.alpha(alpha)
        )
    }
} 