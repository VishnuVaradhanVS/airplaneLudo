package com.example.airplaneludo.presentation.lobby

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.airplaneludo.presentation.GameViewModel
import com.example.airplaneludo.presentation.Screen
import com.example.airplaneludo.ui.theme.Blue3
import com.example.airplaneludo.ui.theme.Red3
import kotlinx.coroutines.launch

@Composable
fun LobbyControl(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel,
    navController: NavController
) {
    val currentRoom = gameViewModel.roomState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val animatedColor by rememberInfiniteTransition().animateColor(
        initialValue = Red3, targetValue = Blue3, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "PulsingWhiteColor"
    )
    val currentDensity = LocalDensity.current
    androidx.compose.runtime.CompositionLocalProvider(
        LocalDensity provides Density(density = currentDensity.density, fontScale = 1f)
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFF1E1E2C).copy(alpha = 0.95f))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "TOKEN COUNT",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "${gameViewModel.baseTokenCount.value}",
                        color = animatedColor,
                        fontSize = 32.sp
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LobbyToken(
                        modifier = Modifier.width(65.dp),
                        count = 4,
                        itemsPerRow = 2,
                        gameViewModel = gameViewModel
                    )
                    LobbyToken(
                        modifier = Modifier.width(80.dp),
                        count = 6,
                        itemsPerRow = 3,
                        gameViewModel = gameViewModel
                    )
                    LobbyToken(
                        modifier = Modifier.width(95.dp),
                        count = 8,
                        itemsPerRow = 4,
                        gameViewModel = gameViewModel
                    )
                    LobbyToken(
                        modifier = Modifier.width(130.dp),
                        count = 12,
                        itemsPerRow = 6,
                        gameViewModel = gameViewModel
                    ) // Plenty of width for 6 dots across
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            gameViewModel.gameAudioManager.playClick()
                            navController.navigate(Screen.GameCore.route)
                            coroutineScope.launch {
                                gameViewModel.startGame()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .shadow(12.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        enabled = gameViewModel.player.value == gameViewModel.host.value && gameViewModel.redTeamPlayers.isNotEmpty() && gameViewModel.blueTeamPlayers.isNotEmpty()
                    ) {
                        Text(
                            text = if (gameViewModel.player.value == gameViewModel.host.value) "START MATCH" else "WAITING",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp
                            ),
                            color = animatedColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LobbyToken(
    modifier: Modifier = Modifier,
    count: Int,
    itemsPerRow: Int,
    gameViewModel: GameViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val animatedColor by rememberInfiniteTransition().animateColor(
        initialValue = Red3, targetValue = Blue3, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "PulsingWhiteColor"
    )
    val isSelected = gameViewModel.baseTokenCount.value == count
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color.White.copy(alpha = 0.08f) else Color.Transparent)
            .clickable(onClick = {
                if (gameViewModel.player.value == gameViewModel.host.value) {
                    gameViewModel.gameAudioManager.playClick()
                    coroutineScope.launch {
                        gameViewModel.baseTokenCount.value = count
                        gameViewModel.changeBaseTokenCount()
                    }
                }
            })
            .padding(vertical = 12.dp)
            .scale(if (isSelected) 1.05f else 1f),
        contentAlignment = Alignment.Center
    ) {
        val rows = (1..count).chunked(itemsPerRow)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (rowItems in rows) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in rowItems) {
                        Icon(
                            imageVector = Icons.Filled.Circle,
                            contentDescription = "Token count",
                            tint = if (isSelected) animatedColor else Color.White,
                            modifier = Modifier
                                .size(14.dp) // Re-added size requirement
                                .padding(horizontal = 1.5.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$count Tokens",
                color = if (isSelected) animatedColor else Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}