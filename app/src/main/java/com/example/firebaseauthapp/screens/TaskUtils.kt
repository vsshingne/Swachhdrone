package com.example.firebaseauthapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.Timestamp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.example.firebaseauthapp.FirebaseUtils

// Data class for a Task
data class Task(
    val id: String = "",
    val address: String = "",
    val assignedTo: String? = null,
    val imageUrl: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val status: String = "",
    val timestamp: Timestamp? = null
)

// Extension function to map Firestore document to Task
fun DocumentSnapshot.toTask(): Task {
    return Task(
        id = id,
        address = getString("address") ?: "",
        assignedTo = getString("assignedTo"),
        imageUrl = getString("imageUrl") ?: "",
        location = getGeoPoint("location") ?: GeoPoint(0.0, 0.0),
        status = getString("status") ?: "",
        timestamp = getTimestamp("timestamp")
    )
}

suspend fun engageTask(task: Task): Result<Unit> {
    return try {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            return Result.failure(Exception("User not authenticated"))
        }
        
        val username = user.displayName ?: user.email?.substringBefore("@") ?: "Unknown"
        val newTask = task.copy(assignedTo = username, status = "ongoing")
        
        // Add to ongoingTasks
        FirebaseUtils.ongoingTasksCollection.document(task.id).set(newTask).await()
        // Remove from activeTasks
        FirebaseUtils.activeTasksCollection.document(task.id).delete().await()
        
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

suspend fun markTaskDone(task: Task): Result<Unit> {
    return try {
        val completedTask = task.copy(status = "completed")
        // Add to completedTasks
        FirebaseUtils.completedTasksCollection.document(task.id).set(completedTask).await()
        // Remove from ongoingTasks
        FirebaseUtils.ongoingTasksCollection.document(task.id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

@Composable
fun TaskListItem(
    task: Task,
    isExpanded: Boolean = false,
    onClick: (() -> Unit)? = null,
    actionButtons: @Composable (() -> Unit)? = null
) {
    val statusColor = when (task.status.lowercase()) {
        "active" -> MaterialTheme.colorScheme.primary
        "ongoing" -> MaterialTheme.colorScheme.tertiary
        "completed" -> MaterialTheme.colorScheme.secondary
        else -> Color.Gray
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick?.invoke() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp)
            ) {
                if (task.imageUrl.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Gray.copy(alpha = 0.1f))
                    ) {
                        AsyncImage(
                            model = task.imageUrl,
                            contentDescription = "Task Image",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Gray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Image",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.address,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = task.status.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = statusColor),
                        shape = RoundedCornerShape(50)
                    )
                    if (!task.assignedTo.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Assigned to: ${task.assignedTo}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            if (isExpanded && actionButtons != null) {
                Divider()
                Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    actionButtons()
                }
            }
        }
    }
} 