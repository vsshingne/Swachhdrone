package com.example.firebaseauthapp.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebaseauthapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.AccountCircle
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.UserProfileChangeRequest
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.navigation.NavController
import com.example.firebaseauthapp.navigation.Screen
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.clickable

@Composable
fun HomeScreen(
    navController: NavController,
    onMapClick: () -> Unit = {},
    onActiveTasksClick: () -> Unit = {},
    onOngoingTasksClick: () -> Unit = {},
    onCompletedTasksClick: () -> Unit = {},
    onAlertsClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val user = remember { FirebaseAuth.getInstance().currentUser }
    var displayName by remember { mutableStateOf(user?.displayName ?: user?.email?.substringBefore("@") ?: "User") }
    var showEditDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(displayName) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var showProfilePicDialog by remember { mutableStateOf(false) }
    var uploading by remember { mutableStateOf(false) }
    var profilePicUrl by remember { mutableStateOf(user?.photoUrl?.toString()) }

    // Image picker launcher
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                uploading = true
                try {
                    val storageRef = Firebase.storage.reference.child("profile_pics/${user?.uid}.jpg")
                    storageRef.putFile(uri).await()
                    val downloadUrl = storageRef.downloadUrl.await().toString()
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse(downloadUrl))
                        .build()
                    user?.updateProfile(profileUpdates)?.addOnSuccessListener {
                        profilePicUrl = downloadUrl
                        showProfilePicDialog = false
                    }
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Failed to upload profile picture."
                } finally {
                    uploading = false
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp)
                    .background(Color(0xFF181A20), RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp))
                    .padding(top = 32.dp, start = 12.dp, end = 12.dp)
            ) {
                // Drawer header
                Text(
                    text = "VSPRIME",
                    color = Color(0xFF00BFFF),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    modifier = Modifier.padding(bottom = 32.dp, start = 8.dp)
                )
                // Drawer items
                DrawerItem(
                    label = "Edit Profile",
                    icon = Icons.Default.Edit,
                    onClick = {
                        showEditDialog = true
                        scope.launch { drawerState.close() }
                    }
                )
                Divider(color = Color(0xFF23242A), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                DrawerItem(
                    label = "Upload Profile Picture",
                    icon = Icons.Default.AccountCircle,
                    onClick = {
                        showProfilePicDialog = true
                        scope.launch { drawerState.close() }
                    }
                )
                Divider(color = Color(0xFF23242A), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                DrawerItem(
                    label = "Change Password",
                    icon = Icons.Default.Lock,
                    onClick = {
                        showChangePasswordDialog = true
                        scope.launch { drawerState.close() }
                    }
                )
                Divider(color = Color(0xFF23242A), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                DrawerItem(
                    label = "Logout",
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        onLogout()
                        scope.launch { drawerState.close() }
                    },
                    highlight = Color(0xFF5D5D5D)
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF2A2A2A))
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hamburger menu at absolute top-left
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = { scope.launch { drawerState.open() } },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.Black)
                    }
                }
                // Profile Picture
                if (profilePicUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(profilePicUrl),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Default Profile Picture",
                        modifier = Modifier.size(80.dp)
                    )
                }
                Text(
                    text = "Welcome, $displayName!",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFFFFFF)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Large Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Do you want to check cleanup tasks?",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color(0xFFA6A6A6),
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = { navController.navigate(Screen.Map.route) },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4265B9))
                        ) {
                            Text("View Map", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                // Category Shortcuts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ShortcutCard("Active Tasks", Icons.Default.List, Color(0xFF00BCD4), { navController.navigate(Screen.ActiveTasks.route) }, Modifier.weight(1f))
                    ShortcutCard("Ongoing Tasks", Icons.Default.Refresh, Color(0xFFFAE859), { navController.navigate(Screen.OngoingTasks.route) }, Modifier.weight(1f))
                    ShortcutCard("Completed Tasks", Icons.Default.CheckCircle, Color(0xFF4CAF50), { navController.navigate(Screen.CompletedTasks.route) }, Modifier.weight(1f))
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Username") },
                        singleLine = true
                    )
                    if (errorMessage.isNotEmpty()) {
                        Text(errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isSaving = true
                        errorMessage = ""
                        val user = FirebaseAuth.getInstance().currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(newName)
                            .build()
                        user?.updateProfile(profileUpdates)?.addOnSuccessListener {
                            displayName = newName
                            showEditDialog = false
                            isSaving = false
                        }?.addOnFailureListener { e ->
                            errorMessage = e.message ?: "Failed to update profile."
                            isSaving = false
                        }
                    },
                    enabled = !isSaving && newName.isNotBlank()
                ) {
                    Text(if (isSaving) "Saving..." else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { Text("Change Password") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        singleLine = true
                    )
                    if (passwordError.isNotEmpty()) {
                        Text(passwordError, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        if (newPassword.length < 6) {
                            passwordError = "Password must be at least 6 characters."
                        } else {
                            currentUser?.updatePassword(newPassword)?.addOnSuccessListener {
                                showChangePasswordDialog = false
                                newPassword = ""
                                passwordError = ""
                            }?.addOnFailureListener { e ->
                                passwordError = e.message ?: "Failed to change password."
                            }
                        }
                    },
                    enabled = newPassword.isNotBlank()
                ) {
                    Text("Change")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showProfilePicDialog) {
        AlertDialog(
            onDismissRequest = { showProfilePicDialog = false },
            title = { Text("Upload Profile Picture") },
            text = {
                if (uploading) {
                    CircularProgressIndicator()
                } else {
                    Text("Pick an image from your gallery to use as your profile picture.")
                }
            },
            confirmButton = {
                Button(
                    onClick = { launcher.launch("image/*") },
                    enabled = !uploading
                ) {
                    Text("Pick Image")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfilePicDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ShortcutCard(label: String, icon: ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.1f else 1f,
        animationSpec = tween(durationMillis = 120),
        finishedListener = {
            if (pressed) {
                pressed = false
                onClick()
            }
        }
    )
    Card(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .height(90.dp)
            .shadow(4.dp, RoundedCornerShape(18.dp))
            .scale(scale),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = { pressed = true }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF1F2937),
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

// DrawerItem composable for consistent styling
@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    highlight: Color? = null
) {
    val highlightColor = highlight ?: Color.Transparent
    val textColor = if (highlight != null) Color.White else Color(0xFFB0E0FF)
    val iconColor = if (highlight != null) Color.White else Color(0xFFB0E0FF)
    Surface(
        color = highlightColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.width(18.dp))
            Text(
                text = label,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
} 