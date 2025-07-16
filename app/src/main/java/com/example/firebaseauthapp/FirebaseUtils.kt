package com.example.firebaseauthapp

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object FirebaseUtils {
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance("swachhdrone") }
    val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    val activeTasksCollection
        get() = firestore.collection("activeTasks")
    val ongoingTasksCollection
        get() = firestore.collection("ongoingTasks")
    val completedTasksCollection
        get() = firestore.collection("completedTasks")
} 