package com.example.uni_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.uni_project.screens.SplashScreen
import com.example.uni_project.core.Route
import com.example.uni_project.ui.theme.Uni_projectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Uni_projectTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Route.SplashScreen
                ) {
                    composable<Route.SplashScreen> {
                        SplashScreen()
                    }


                }
            }


        }
    }
}

