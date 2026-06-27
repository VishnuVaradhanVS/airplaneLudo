package com.example.airplaneludo

import android.app.Application
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.airplaneludo.presentation.GameViewModel
import com.example.airplaneludo.presentation.Screen
import com.example.airplaneludo.presentation.dashboard.DashBoard
import com.example.airplaneludo.presentation.game.GameCore
import com.example.airplaneludo.presentation.lobby.Lobby
import com.example.airplaneludo.ui.theme.AirplaneLudoTheme

class MainActivity : ComponentActivity() {
    fun enableImmersiveMode() {
        val window = window ?: return
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableEdgeToEdge()
        setContent {
            enableImmersiveMode()
            AirplaneLudoTheme {
                val gameViewModel: GameViewModel = viewModel()
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
                    composable(Screen.Dashboard.route) {
                        DashBoard(modifier = Modifier, navController,gameViewModel)
                    }
                    composable(Screen.Lobby.route) {
                        Lobby(modifier = Modifier, navController, gameViewModel)
                    }
                    composable(Screen.GameCore.route) {
                        GameCore(modifier = Modifier, navController, gameViewModel)
                    }
                }
            }
        }
    }
}