package com.example.uni_project.core.data_class

import androidx.annotation.DrawableRes
import com.example.uni_project.R
import com.example.uni_project.core.Route

sealed class BottomNavItem(
    val route: String,
    @DrawableRes val icon: Int
) {
    object Home : BottomNavItem(Route.HomeScreen.route,  R.drawable.home)
    object Favourite : BottomNavItem(Route.FavouriteScreen.route,  R.drawable.booking)
    object Settings : BottomNavItem(Route.SettingsScreen.route,  R.drawable.settings)

    companion object {
        fun getItems(): List<BottomNavItem> = listOf(Home, Favourite, Settings)


        fun isMainScreen(route: String?): Boolean {
            if (route == null) return false


            val baseRoute = route.substringBefore('?')

            val mainRoutes = getItems().map { it.route } + Route.SearchResultScreen.route + Route.ProfileScreen.route
            return baseRoute in mainRoutes
        }
}}