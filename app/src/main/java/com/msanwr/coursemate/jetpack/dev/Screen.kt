package com.msanwr.coursemate.jetpack.dev

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Favorites : Screen("favorites")
    data object Settings : Screen("settings")
    data object Login: Screen("login")
}