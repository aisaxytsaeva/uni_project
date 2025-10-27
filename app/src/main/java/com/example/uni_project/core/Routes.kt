package com.example.uni_project.core

import kotlinx.serialization.*


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
    @Serializable
    data object SettingsScreen: Route {
        override val route: String = "SettingsScreen"
    }

    @Serializable
    data object ProfileScreen: Route{
        override val route: String = "ProfileScreen"
    }

    @Serializable
    data object SearchResultScreen: Route{
        override val route: String = "SearchResultScreen"
    }

    @Serializable
    data object FavouriteScreen: Route{
        override val route: String = "FavouriteScreen"
    }
}
