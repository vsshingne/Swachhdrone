package com.example.firebaseauthapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.firebaseauthapp.screens.login.LoginScreen
import com.example.firebaseauthapp.screens.signup.SignupScreen
import com.google.firebase.auth.FirebaseAuth
import com.example.firebaseauthapp.screens.ActiveTasksScreen
import com.example.firebaseauthapp.screens.OngoingTasksScreen
import com.example.firebaseauthapp.screens.CompletedTasksScreen
import com.example.firebaseauthapp.screens.profile.HomeScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Map : Screen("map")
    object MapWithLocation : Screen("map/{lat}/{lng}") {
        fun createRoute(lat: Double, lng: Double) = "map/${lat.toFloat()}/${lng.toFloat()}"
    }
    object ActiveTasks : Screen("active_tasks")
    object OngoingTasks : Screen("ongoing_tasks")
    object CompletedTasks : Screen("completed_tasks")
    object Profile : Screen("profile")
}

@Composable
fun NavGraph(navController: NavHostController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startDestination = if (currentUser != null) Screen.Profile.route else Screen.Login.route
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignup = {
                    navController.navigate(Screen.Signup.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Signup.route) {
            SignupScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                },
                onSignupSuccess = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Map.route) {
            com.example.firebaseauthapp.screens.map.MapScreen()
        }
        composable(
            route = Screen.MapWithLocation.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lng") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
            val lng = backStackEntry.arguments?.getFloat("lng")?.toDouble() ?: 0.0
            com.example.firebaseauthapp.screens.map.MapScreen(focusLat = lat, focusLng = lng)
        }
        composable(Screen.ActiveTasks.route) {
            ActiveTasksScreen(navController)
        }
        composable(Screen.OngoingTasks.route) {
            OngoingTasksScreen(navController)
        }
        composable(Screen.CompletedTasks.route) {
            CompletedTasksScreen(navController)
        }
        composable(Screen.Profile.route) {
            com.example.firebaseauthapp.screens.profile.HomeScreen(navController)
        }
    }
} 