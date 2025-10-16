package com.example.uni_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.uni_project.core.AppPreferences
import com.example.uni_project.presentation.screens.SplashScreen
import com.example.uni_project.core.Route
import com.example.uni_project.core.rememberNetworkState
import com.example.uni_project.presentation.screens.AuthorizationScreen
import com.example.uni_project.presentation.screens.CongratulationScreen
import com.example.uni_project.presentation.screens.DocumentUploadScreen
import com.example.uni_project.presentation.screens.GettingStartedScreen
import com.example.uni_project.presentation.screens.Greetings
import com.example.uni_project.presentation.screens.NoInternetScreen
import com.example.uni_project.presentation.screens.RegistrationDetailsScreen
import com.example.uni_project.presentation.screens.RegistrationScreen
import com.example.uni_project.presentation.viewmodel.DocumentUploadViewModel
import com.example.uni_project.presentation.viewmodel.RegistrationDetailsViewModel
import com.example.uni_project.presentation.viewmodel.RegistrationViewModel
import com.example.uni_project.ui.theme.Uni_projectTheme
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.uni_project.core.AuthRepositoryImpl
import com.example.uni_project.core.GoogleAuthService
import com.example.uni_project.core.SessionManager
import com.example.uni_project.core.data_class.RegistrationResult
import com.example.uni_project.dao.AppDatabase
import com.example.uni_project.presentation.screens.HomeScreen
import com.example.uni_project.presentation.viewmodel.AuthViewModel
import com.example.uni_project.presentation.viewmodel.factory.AuthViewModelFactory
import com.example.uni_project.presentation.viewmodel.factory.DocumentUploadViewModelFactory
import com.example.uni_project.presentation.viewmodel.factory.RegistrationDetailsViewModelFactory
import com.example.uni_project.presentation.viewmodel.factory.RegistrationViewModelFactory
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appDatabase = AppDatabase.getInstance(this)
        val googleAuthService = GoogleAuthService(this)
        val sessionManager = SessionManager(this)

        setContent {
            Uni_projectTheme {
                val navController = rememberNavController()
                val isOnline by rememberNetworkState()
                val appPreferences = remember { AppPreferences(this) }
                val authRepository = remember {
                    AuthRepositoryImpl(
                        googleAuthService = googleAuthService,
                        database = appDatabase,
                        sessionManager = sessionManager
                    )
                }
                LaunchedEffect(Unit) {
                    val currentUser = authRepository.getCurrentUser()
                    println("DEBUG AppStart: Current user: ${currentUser?.email}")
                    println("DEBUG AppStart: Is logged in: ${sessionManager.isLoggedIn()}")

                    if (currentUser != null && sessionManager.isLoggedIn()) {
                        navController.navigate(Route.HomeScreen.route) {
                            popUpTo(Route.SplashScreen.route) { inclusive = true }
                        }
                    }
                }

                LaunchedEffect(isOnline) {
                    val currentDestination = navController.currentDestination
                    val isNoInternetScreen = currentDestination?.route == Route.NoInternet.route

                    if (!isOnline && !isNoInternetScreen) {
                        navController.navigate(Route.NoInternet.route) {
                            launchSingleTop = true
                        }
                    } else if (isOnline && isNoInternetScreen) {
                        navController.popBackStack()
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Route.SplashScreen.route
                ) {
                    composable(Route.SplashScreen.route) {
                        SplashScreen(
                            onSplashComplete = {
                                if (isOnline) {
                                    if (appPreferences.isFirstLaunch) {
                                        navController.navigate(Route.Greetings.route) {
                                            popUpTo(Route.SplashScreen.route) {
                                                inclusive = true
                                            }
                                        }
                                    } else {
                                        navController.navigate(Route.Autorization.route) {
                                            popUpTo(Route.SplashScreen.route) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                } else {
                                    navController.navigate(Route.NoInternet.route) {
                                        popUpTo(Route.SplashScreen.route) {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                        )
                    }

                    composable(Route.Greetings.route) {
                        Greetings(
                            onFinish = {
                                appPreferences.isFirstLaunch = false
                                navController.navigate(Route.GettingStarted.route) {
                                    popUpTo(Route.Greetings.route) {
                                        inclusive = true
                                    }
                                }
                            },
                            onSkip = {
                                appPreferences.isFirstLaunch = false
                                navController.navigate(Route.GettingStarted.route) {
                                    popUpTo(Route.Greetings.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    composable(Route.GettingStarted.route) {
                        GettingStartedScreen(
                            onRegistrationClick = { navController.navigate(Route.Registration1.route) },
                            onAuthorizationClick = { navController.navigate(Route.Autorization.route) }
                        )
                    }

                    composable(Route.Autorization.route) {
                        val authViewModel: AuthViewModel = viewModel(
                            factory = AuthViewModelFactory(authRepository, sessionManager)
                        )

                        AuthorizationScreen(
                            onAuthorization = { navController.navigate(Route.HomeScreen.route) },
                            onRegistration = { navController.navigate(Route.Registration1.route) },
                            authViewModel = authViewModel
                        )
                    }

                    composable(Route.Registration1.route) {
                        val viewModel: RegistrationViewModel = viewModel(
                            factory = RegistrationViewModelFactory(authRepository, sessionManager)
                        )
                        val scope = rememberCoroutineScope()

                        LaunchedEffect(viewModel.registrationResult) {
                            viewModel.registrationResult.collect { result ->
                                when (result) {
                                    is RegistrationResult.Success -> {
                                        val email = viewModel.registrationState.value.email
                                        println("DEBUG Registration1: Navigating with email: $email")
                                        navController.navigate(Route.Registration2.createRoute(email))
                                    }
                                    is RegistrationResult.Error -> {}
                                    null -> {}
                                }
                            }
                        }

                        RegistrationScreen(
                            onBack = { navController.popBackStack() },
                            onRegister = { email, password ->
                                viewModel.updateEmail(email)
                                viewModel.updatePassword(password)
                                viewModel.updateConfirmPassword(password)

                                scope.launch {
                                    viewModel.registerStep1()
                                }
                            },
                            sessionManager = sessionManager
                        )
                    }


                    composable(
                        route = "${Route.Registration2.route}/{email}",
                        arguments = listOf(
                            navArgument("email") {
                                type = NavType.StringType
                                defaultValue = ""
                            }
                        )
                    ) { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        val detailsViewModel: RegistrationDetailsViewModel = viewModel(
                            factory = RegistrationDetailsViewModelFactory(authRepository, sessionManager)
                        )

                        RegistrationDetailsScreen(
                            email = email,
                            onBack = { navController.popBackStack() },
                            onRegistrationCont = {
                                navController.navigate("${Route.Registration3.route}/$email")
                            },
                            authRepository = authRepository,
                            detailsViewModel = detailsViewModel
                        )
                    }


                    composable(
                        route = "${Route.Registration3.route}/{email}",
                        arguments = listOf(
                            navArgument("email") {
                                type = NavType.StringType
                                defaultValue = ""
                            }
                        )
                    ) { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        val uploadViewModel: DocumentUploadViewModel = viewModel(
                            factory = DocumentUploadViewModelFactory(authRepository, sessionManager)
                        )
                        val scope = rememberCoroutineScope()

                        println("DEBUG Registration3: Email from route: '$email'")

                        LaunchedEffect(uploadViewModel.registrationResult) {
                            uploadViewModel.registrationResult.collect { result ->
                                when (result) {
                                    is RegistrationResult.Success -> {
                                        println("DEBUG Registration3: Registration SUCCESS")
                                        navController.navigate(Route.Congaratulation.route) {
                                            popUpTo(Route.Registration3.route) { inclusive = true }
                                        }
                                    }
                                    is RegistrationResult.Error -> {
                                        println("DEBUG Registration3: Registration FAILED: ${result.message}")
                                    }
                                    null -> {}
                                }
                            }
                        }

                        DocumentUploadScreen(
                            email = email,
                            onBack = { navController.popBackStack() },
                            onRegistrationComplete = {
                                scope.launch {
                                    uploadViewModel.completeRegistration(email)
                                }
                            },
                            sessionManager = sessionManager
                        )
                    }
                    composable(Route.Congaratulation.route) {
                        CongratulationScreen(
                            onClick = {
                                navController.navigate(Route.HomeScreen.route) {
                                    popUpTo(0)
                                }
                            }
                        )
                    }

                    composable(Route.HomeScreen.route) {
                        HomeScreen()
                    }

                    composable(Route.NoInternet.route) {
                        NoInternetScreen(
                            onRetry = {
                                if (isOnline) {
                                    navController.popBackStack()
                                    if (navController.currentBackStackEntry == null) {
                                        if (appPreferences.isFirstLaunch) {
                                            navController.navigate(Route.Greetings.route)
                                        } else {
                                            navController.navigate(Route.Autorization.route)
                                        }
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