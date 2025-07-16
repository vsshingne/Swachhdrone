package com.example.firebaseauthapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.firebaseauthapp.ui.theme.FirebaseAuthAppTheme
import com.example.firebaseauthapp.navigation.NavGraph
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import android.os.Build
import androidx.core.view.WindowInsetsControllerCompat

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Make status bar transparent and set light icons
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        setContent {
            FirebaseAuthAppTheme {
                val navController = rememberNavController()
                
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { },
                            navigationIcon = {
                                IconButton(onClick = { }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Menu",
                                        tint = Color.Black
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        )
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        NavGraph(navController = navController)
                    }
                }
            }
        }
    }
}