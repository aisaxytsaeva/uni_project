package com.example.uni_project.core

import kotlinx.serialization.*


sealed interface Route {
    @Serializable
    data object SplashScreen: Route
}
