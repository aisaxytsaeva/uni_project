package com.example.uni_project.core

import androidx.navigation.NavBackStackEntry
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.net.URLEncoder


sealed interface Route {
    val route: String

    @Serializable
    data object SplashScreen: Route {
        override val route: String = "SplashScreen"
    }

    @Serializable
    data object NoInternet: Route {
        override val route: String = "NoInternet"
    }

    @Serializable
    data object Greetings: Route {
        override val route: String = "Greetings"
    }

    @Serializable
    data object GettingStarted: Route {
        override val route: String = "GettingStarted"
    }

    @Serializable
    data object Autorization: Route {
        override val route: String = "Autorization"
    }

    @Serializable
    data object Registration1: Route {
        override val route: String = "Registration1"
    }

    @Serializable
    data object Registration2: Route {
        override val route: String = "Registration2"
        fun createRoute(email: String) = "Registration2/$email"
    }

    @Serializable
    data object Registration3: Route {
        override val route: String = "Registration3"
    }

    @Serializable
    data object Congaratulation: Route {
        override val route: String = "Congaratulation"
    }

    @Serializable
    data object HomeScreen: Route {
        override val route: String = "HomeScreen"
    }
}
fun Route.Registration2.withEmail(email: String): String {
    return "Registration2?email=${URLEncoder.encode(email, "UTF-8")}"
}

fun Route.Registration2.getEmail(backStackEntry: NavBackStackEntry): String {
    return backStackEntry.arguments?.getString("email") ?: ""
}