package com.example.uni_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.uni_project.presentation.screens.SplashScreen
import com.example.uni_project.core.Route
import com.example.uni_project.core.rememberNetworkState
import com.example.uni_project.presentation.screens.NoInternetScreen
import com.example.uni_project.ui.theme.Uni_projectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Uni_projectTheme {
                val navController = rememberNavController()
                val isOnline by rememberNetworkState()


                LaunchedEffect(isOnline) {
                    val currentDestination = navController.currentDestination
                    val isNoInternetScreen = currentDestination?.hasRoute<Route.NoInternet>() == true

                    if (!isOnline && !isNoInternetScreen) {
                        navController.navigate(Route.NoInternet) {
                            launchSingleTop = true
                        }
                    } else if (isOnline && isNoInternetScreen) {
                        navController.popBackStack()
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Route.SplashScreen
                ) {
                    composable<Route.SplashScreen> {
                        SplashScreen(
                            onSplashComplete = {
                                if (isOnline) {
                                    navController.navigate(Route.Autorization) {
                                        popUpTo<Route.SplashScreen> {
                                            inclusive = true
                                        }
                                    }
                                } else {
                                    navController.navigate(Route.NoInternet) {
                                        popUpTo<Route.SplashScreen> {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                        )
                    }

                    composable<Route.Autorization> {

                    }

                    composable<Route.NoInternet> {
                        NoInternetScreen(
                            onRetry = {

                                if (isOnline) {
                                    navController.popBackStack()
                                    if (navController.currentBackStackEntry == null) {
                                        navController.navigate(Route.Autorization)
                                        }
                                    }

                            }
                        )
                    }

                }
            }
        }
    }
}