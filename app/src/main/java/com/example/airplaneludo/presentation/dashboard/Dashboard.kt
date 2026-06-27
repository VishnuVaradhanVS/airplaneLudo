package com.example.airplaneludo.presentation.dashboard

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.airplaneludo.presentation.GameViewModel
import com.example.airplaneludo.presentation.Screen
import com.example.airplaneludo.ui.theme.Blue3
import com.example.airplaneludo.ui.theme.Blue5
import com.example.airplaneludo.ui.theme.Red3
import com.example.airplaneludo.ui.theme.Red5
import kotlin.math.cos
import kotlin.math.sin

data class RandomTokenEngine(
    val id: Int,
    val initialXRatio: Float,
    val initialYRatio: Float,
    val movementSpeed: Float,
    val isRedTeam: Boolean,
    val amplitude: Float,
    val phaseShift: Float
)

@Composable
fun DashBoard(
    modifier: Modifier = Modifier,
    navController: NavController,
    gameViewModel: GameViewModel
) {
    gameViewModel.showLobbyInfo.value = false
    if (gameViewModel.musicEnabled.value) gameViewModel.gameAudioManager.startBackgroundMusic()
    val showJoinRoomOnline = gameViewModel.showJoinRoomOnline
    val showJoinRoomLocal = gameViewModel.showJoinRoomLocal
    val showHelp = gameViewModel.showHelp
    val infiniteTransition = rememberInfiniteTransition(label = "IconColorTransition")
    val animatedIconColor by infiniteTransition.animateColor(
        initialValue = Red3,
        targetValue = Blue3,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IconColorAnimation"
    )
    val globalTimeClock by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "BackgroundPhysicsClock"
    )
    val proceduralTokens = remember {
        listOf(
            RandomTokenEngine(0, 0.05f, 0.20f, 1.1f, true, 80f, 0.5f),
            RandomTokenEngine(1, 0.35f, 0.10f, 0.7f, false, 120f, 1.9f),
            RandomTokenEngine(2, 0.65f, 0.35f, 1.4f, true, 60f, 3.1f),
            RandomTokenEngine(3, 0.15f, 0.75f, 0.9f, false, 100f, 0.8f),
            RandomTokenEngine(4, 0.80f, 0.85f, 1.2f, true, 130f, 2.3f),
            RandomTokenEngine(5, 0.50f, 0.65f, 0.6f, false, 75f, 1.4f),
            RandomTokenEngine(6, 0.90f, 0.30f, 1.5f, true, 95f, 0.2f),
            RandomTokenEngine(7, 0.25f, 0.50f, 0.8f, false, 110f, 2.7f)
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Red5, Blue5),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height
            for (token in proceduralTokens) {
                val progressiveDistanceX = globalTimeClock * 1.5f * token.movementSpeed
                val originalLocationX = canvasW * token.initialXRatio
                var currentTokenX =
                    (originalLocationX + progressiveDistanceX) % (canvasW + 240f) - 120f
                if (currentTokenX < -120f) currentTokenX = canvasW + 120f
                val principalWave = sin((currentTokenX * 0.004f) + token.phaseShift)
                val variationHarmonicNoise =
                    cos((currentTokenX * 0.009f) + (token.id * 3.5f)) * 0.25f
                val originalLocationY = canvasH * token.initialYRatio
                val currentTokenY =
                    originalLocationY + ((principalWave + variationHarmonicNoise) * token.amplitude)
                val teamTokenColor = if (token.isRedTeam) Red3 else Blue3
                val internalCoreRadius = 16.dp.toPx()
                val breathingPulseAnimation =
                    sin((globalTimeClock * 0.07f) + token.id) * 4.dp.toPx()
                val externalNeonGlowRadius = 38.dp.toPx() + breathingPulseAnimation
                drawCircle(
                    color = teamTokenColor.copy(alpha = 0.12f),
                    radius = externalNeonGlowRadius,
                    center = Offset(currentTokenX, currentTokenY)
                )
                drawCircle(
                    color = teamTokenColor.copy(alpha = 0.22f),
                    radius = internalCoreRadius * 1.45f,
                    center = Offset(currentTokenX, currentTokenY)
                )
                drawCircle(
                    color = teamTokenColor.copy(alpha = 0.70f),
                    radius = internalCoreRadius,
                    center = Offset(currentTokenX, currentTokenY)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.55f),
                    radius = internalCoreRadius * 0.32f,
                    center = Offset(
                        currentTokenX - (internalCoreRadius * 0.3f),
                        currentTokenY - (internalCoreRadius * 0.3f)
                    )
                )
            }
        }
        if (showJoinRoomOnline.value) {
            JoinRoomOnline(
                modifier = Modifier,
                navController = navController,
                gameViewModel = gameViewModel
            )
        } else if (showJoinRoomLocal.value) {
            JoinRoomLocal(
                modifier = Modifier,
                navController = navController,
                gameViewModel = gameViewModel
            )
        } else if (showHelp.value) {
            Rules(modifier = Modifier, gameViewModel)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "AIRPLANE LUDO",
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(Red3, Blue3),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, 0f)
                        ),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "profile",
                    tint = animatedIconColor,
                    modifier = Modifier.size(180.dp)
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp, 0.dp)
                        .height(60.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp)),
                    value = gameViewModel.userName.value ?: "",
                    onValueChange = {
                        gameViewModel.userName.value = it
                        if (it == "") gameViewModel.userName.value = null
                    },
                    textStyle = TextStyle(
                        color = animatedIconColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    ),
                    placeholder = {
                        Text(
                            text = "PLAYER NAME",
                            color = Color.White.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E1E2C).copy(alpha = 0.95f),
                        unfocusedContainerColor = Color(0xFF1E1E2C).copy(alpha = 0.95f),
                        disabledContainerColor = Color.White.copy(alpha = 0.1f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(32.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "ONLINE MULTIPLAYER", style = TextStyle(
                                brush = Brush.linearGradient(
                                    colors = listOf(Red3, Blue3),
                                    start = Offset(0f, 0f),
                                    end = Offset(Float.POSITIVE_INFINITY, 0f)
                                ),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    gameViewModel.gameAudioManager.playClick()
                                    gameViewModel.showLobbyInfo.value = true
                                    gameViewModel.lobbyMessage.value =
                                        "Server offline. Try again later"
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(8.dp, RoundedCornerShape(16.dp))
                                    .weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Red3
                                )
                            ) {
                                Text(
                                    text = "CREATE",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    gameViewModel.gameAudioManager.playClick()
                                    showJoinRoomOnline.value = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(8.dp, RoundedCornerShape(16.dp))
                                    .weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color.Blue
                                )
                            ) {
                                Text(
                                    text = "JOIN",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "LOCAL MULTIPLAYER", style = TextStyle(
                                brush = Brush.linearGradient(
                                    colors = listOf(Red3, Blue3),
                                    start = Offset(0f, 0f),
                                    end = Offset(Float.POSITIVE_INFINITY, 0f)
                                ),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    gameViewModel.gameAudioManager.playClick()
                                    navController.navigate(Screen.Lobby.route)
                                    gameViewModel.hostLocalNetworkRoom()
                                    println("create local game")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(8.dp, RoundedCornerShape(16.dp))
                                    .weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Red3
                                )
                            ) {
                                Text(
                                    text = "CREATE",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    gameViewModel.gameAudioManager.playClick()
                                    showJoinRoomLocal.value = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(8.dp, RoundedCornerShape(16.dp))
                                    .weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color.Blue
                                )
                            ) {
                                Text(
                                    text = "JOIN",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    gameViewModel.gameAudioManager.playClick()
                                    gameViewModel.showHelp.value = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(8.dp, RoundedCornerShape(16.dp))
                                    .weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1E1E2C).copy(alpha = 0.95f),
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "RULES",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    gameViewModel.gameAudioManager.playClick()
                                    gameViewModel.musicEnabled.value =
                                        !gameViewModel.musicEnabled.value
                                    if (gameViewModel.musicEnabled.value) {
                                        gameViewModel.gameAudioManager.startBackgroundMusic()
                                    } else {
                                        gameViewModel.gameAudioManager.stopBackgroundMusic()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(8.dp, RoundedCornerShape(16.dp))
                                    .weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (gameViewModel.musicEnabled.value) animatedIconColor else Color(
                                        0xFF1E1E2C
                                    ).copy(alpha = 0.95f),
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "MUSIC",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}