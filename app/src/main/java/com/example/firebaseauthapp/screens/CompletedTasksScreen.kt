package com.example.firebaseauthapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.firebaseauthapp.FirebaseUtils
import com.example.firebaseauthapp.screens.Task
import com.example.firebaseauthapp.screens.toTask
import com.example.firebaseauthapp.screens.TaskListItem
import kotlinx.coroutines.tasks.await
import androidx.navigation.NavController
import com.example.firebaseauthapp.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedTasksScreen(navController: NavController) {
    var tasks by remember { mutableStateOf(listOf<Task>()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var expandedTaskId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        loading = true
        error = null
        try {
            val result = FirebaseUtils.completedTasksCollection.get().await()
            tasks = result.documents.map { doc -> doc.toTask() }
        } catch (e: Exception) {
            error = e.localizedMessage
        }
        loading = false
    }

    Scaffold(
        containerColor = Color(0xFFE6F7F1) // faint green
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Text(
                    text = "Completed Tasks",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Color.Black),
                    modifier = Modifier.padding(24.dp)
                )
            }
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    error != null -> Text("Error: $error", color = MaterialTheme.colorScheme.error)
                    tasks.isEmpty() -> Text("No tasks found", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                    else -> LazyColumn {
                        items(tasks) { task ->
                            TaskListItem(
                                task = task,
                                isExpanded = expandedTaskId == task.id,
                                onClick = {
                                    expandedTaskId = if (expandedTaskId == task.id) null else task.id
                                },
                                actionButtons = if (expandedTaskId == task.id) {
                                    {
                                        Button(onClick = {
                                            val lat = task.location.latitude
                                            val lng = task.location.longitude
                                            navController.navigate(Screen.MapWithLocation.createRoute(lat, lng))
                                        }) {
                                            Text("Map")
                                        }
                                    }
                                } else null
                            )
                        }
                    }
                }
            }
        }
    }
} 