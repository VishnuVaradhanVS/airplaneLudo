package com.example.airplaneludo.presentation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object GameCore : Screen("gamecore")
    object Lobby : Screen("lobby")
}