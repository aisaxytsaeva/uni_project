package com.example.uni_project.core

import kotlinx.serialization.*


sealed interface Route {
    @Serializable
    data object SplashScreen: Route

    @Serializable
    data object NoInternet: Route

    @Serializable
    data object Greetings: Route

    @Serializable
    data object Autorization: Route

    @Serializable
    data object Registration1: Route

    @Serializable
    data object Registration2: Route

    @Serializable
    data object Registartion3: Route

    @Serializable
    data object Congaratulation: Route


}
