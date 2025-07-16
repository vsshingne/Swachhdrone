package com.example.firebaseauthapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import android.util.Log
import androidx.navigation.NavController
import com.example.firebaseauthapp.navigation.Screen
import androidx.compose.material3.AlertDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OngoingTasksScreen(navController: NavController) {
    var tasks by remember { mutableStateOf(listOf<Task>()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var expandedTaskId by remember { mutableStateOf<String?>(null) }
    var markDoneDialogTask by remember { mutableStateOf<Task?>(null) }
    var markDoneLoading by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var showErrorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    fun refreshTasks() {
        coroutineScope.launch {
            loading = true
            error = null
            try {
                val result = FirebaseUtils.ongoingTasksCollection.get().await()
                tasks = result.documents.map { doc -> doc.toTask() }
            } catch (e: Exception) {
                error = e.localizedMessage
            }
            loading = false
        }
    }

    LaunchedEffect(Unit) { refreshTasks() }
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage) {
            snackbarHostState.showSnackbar("Task marked as done successfully!")
            showSuccessMessage = false
        }
    }
    
    LaunchedEffect(showErrorMessage) {
        showErrorMessage?.let { error ->
            snackbarHostState.showSnackbar("Error: $error")
            showErrorMessage = null
        }
    }

    Scaffold(
        containerColor = Color(0xFFFFF3E0),
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                    text = "Ongoing Tasks",
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
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Button(onClick = {
                                                markDoneDialogTask = task
                                            }, enabled = !markDoneLoading) {
                                                Text("Mark as done")
                                            }
                                            Button(onClick = {
                                                val lat = task.location.latitude
                                                val lng = task.location.longitude
                                                navController.navigate(Screen.MapWithLocation.createRoute(lat, lng))
                                            }) {
                                                Text("Map")
                                            }
                                        }
                                    }
                                } else null
                            )
                        }
                    }
                }
            }
        }
        if (markDoneDialogTask != null) {
            AlertDialog(
                onDismissRequest = { if (!markDoneLoading) markDoneDialogTask = null },
                title = { Text("Mark as Done") },
                text = { Text("Do you want to mark this task as done? It will be moved to Completed Tasks.") },
                confirmButton = {
                    Button(onClick = {
                        markDoneLoading = true
                        coroutineScope.launch {
                            try {
                                val result = markTaskDone(markDoneDialogTask!!)
                                if (result.isSuccess) {
                                    showSuccessMessage = true
                                    refreshTasks()
                                } else {
                                    val errorMsg = result.exceptionOrNull()?.localizedMessage ?: "Failed to mark task as done"
                                    showErrorMessage = errorMsg
                                }
                            } catch (e: Exception) {
                                val errorMsg = e.localizedMessage ?: "Unknown error occurred"
                                showErrorMessage = errorMsg
                            } finally {
                                markDoneLoading = false
                                markDoneDialogTask = null
                            }
                        }
                    }, enabled = !markDoneLoading) {
                        if (markDoneLoading) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        else Text("Yes")
                    }
                },
                dismissButton = {
                    Button(onClick = { markDoneDialogTask = null }, enabled = !markDoneLoading) { Text("No") }
                }
            )
        }
    }
} 