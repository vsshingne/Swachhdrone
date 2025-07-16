package com.example.firebaseauthapp

import android.app.Application
import com.google.firebase.FirebaseApp

class HackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
} 