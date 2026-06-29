package com.example.airplaneludo.presentation.lobby

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.airplaneludo.presentation.GameViewModel
import com.example.airplaneludo.presentation.Screen
import com.example.airplaneludo.ui.theme.Blue3
import com.example.airplaneludo.ui.theme.Blue4
import com.example.airplaneludo.ui.theme.Blue5
import com.example.airplaneludo.ui.theme.Red3
import com.example.airplaneludo.ui.theme.Red4
import com.example.airplaneludo.ui.theme.Red5
import com.example.shared.data.Player
import com.example.shared.data.Team
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Lobby(
    modifier: Modifier = Modifier,
    navController: NavController,
    gameViewModel: GameViewModel
) {
    if (gameViewModel.startGame.value) {
        navController.navigate(Screen.GameCore.route)
        gameViewModel.startGame.value = false
    }
    val coroutineScope = rememberCoroutineScope()
    val currentRoom = gameViewModel.roomState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "RoomCodePulse")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = Red3, targetValue = Blue3, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "PulsingWhiteColor"
    )
    val redTeamPlayers = currentRoom.value?.redTeam ?: emptyList()
    val blueTeamPlayers = currentRoom.value?.blueTeam ?: emptyList()
    BackHandler(enabled = true) {
        coroutineScope.launch {
            gameViewModel.shutdownHostedRoom()
            navController.navigate(Screen.Dashboard.route)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Red5, Blue5),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, 0f)
                    )
                )
        ) {

        }
        if (gameViewModel.showLobbyInfo.value) {
            LobbyInfo(
                modifier = Modifier,
                gameViewModel,
                navController,
                gameViewModel.lobbyMessage.value
            )
        } else {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 32.dp, 16.dp, 0.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (gameViewModel.host.value != null) "${gameViewModel.host.value?.name ?: " "}'s ROOM" else "",
                        modifier = Modifier
                            .weight(1f),
                        textAlign = TextAlign.Center,
                        color = Color(0xFF1E1E2C).copy(alpha = 0.95f),
                        fontSize = 24.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            gameViewModel.gameAudioManager.playClick()
                            gameViewModel.shutdownHostedRoom()
                        },
                        colors = ButtonColors(
                            containerColor = Color(0xFF1E1E2C).copy(alpha = 0.95f),
                            contentColor = Color.White,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = Color.Transparent
                        ),
                    ) {
                        Text("EXIT")
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .border(3.dp, animatedColor, RoundedCornerShape(32.dp))
                        .background(Color(0xFF1E1E2C).copy(alpha = 0.95f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ROOM CODE",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "${gameViewModel.roomId.value}",
                            style = TextStyle(
                                color = animatedColor,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "RED TEAM",
                            style = TextStyle(
                                color = Red3,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyColumn() {
                            items(items = redTeamPlayers, key = { player -> player.id }) { player ->
                                TeamPlayerSlot(player = player, gameViewModel)
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "BLUE TEAM",
                            style = TextStyle(
                                color = Blue3,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyColumn() {
                            items(
                                items = blueTeamPlayers,
                                key = { player -> player.id }) { player ->
                                TeamPlayerSlot(player = player, gameViewModel)
                            }
                        }
                    }
                }
                LobbyControl(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    gameViewModel,
                    navController
                )
            }
        }
    }
}

@Composable
fun TeamPlayerSlot(player: Player, gameViewModel: GameViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val showOptions = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (gameViewModel.player.value == player) Color(0xFF1E1E2C).copy(alpha = 0.85f) else
                    Color.White.copy(
                        alpha = 0.2f
                    )
            )
            .border(
                2.dp,
                Color.White.copy(alpha = 0.7f),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
            .combinedClickable(onClick = {
                gameViewModel.gameAudioManager.playClick()
                showOptions.value = false
            }, onLongClick = {
                gameViewModel.gameAudioManager.playClick()
                if (player != gameViewModel.player.value && gameViewModel.player.value?.id == gameViewModel.host.value?.id)
                    showOptions.value = true
            })
            .scale(if (gameViewModel.player.value == player) 1.2f else 1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (gameViewModel.player.value == player && player.team == Team.BLUE) {
                Icon(
                    imageVector = Icons.Filled.KeyboardDoubleArrowLeft,
                    contentDescription = "Swap Team",
                    tint = Red3,
                    modifier = Modifier
                        .clickable(onClick = {
                            coroutineScope.launch {
                                gameViewModel.gameAudioManager.playClick()
                                gameViewModel.swapTeam(gameViewModel.player.value!!)
                            }
                        })
                        .weight(.3f)
                )
            }

            Text(
                text = player.name,
                style = TextStyle(
                    color = if (player.team == Team.RED) Red3 else Blue3,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        if (player.team == Team.RED) 8.dp else 0.dp,
                        0.dp,
                        if (player.team == Team.RED) 0.dp else 8.dp,
                        0.dp
                    )
            )
            if (gameViewModel.player.value == player && player.team == Team.RED) {
                Icon(
                    imageVector = Icons.Filled.KeyboardDoubleArrowRight,
                    contentDescription = "Swap Team",
                    tint = Blue3,
                    modifier = Modifier
                        .clickable(onClick = {
                            coroutineScope.launch {
                                gameViewModel.gameAudioManager.playClick()
                                gameViewModel.swapTeam(gameViewModel.player.value!!)
                            }
                        })
                        .weight(.3f)
                )
            }
        }
        if (showOptions.value) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, 0.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (player.team == Team.RED) Blue4 else Red4)
                        .padding(8.dp)
                        .weight(1f)
                        .clickable(onClick = {
                            gameViewModel.gameAudioManager.playClick()
                            coroutineScope.launch { gameViewModel.swapTeam(player) }
                            showOptions.value = false
                        }),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SWAP",
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                        color = Color(0xFF1E1E2C).copy(alpha = 0.95f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E1E2C).copy(alpha = 0.95f))
                        .padding(8.dp)
                        .weight(1f)
                        .clickable(onClick = {
                            gameViewModel.gameAudioManager.playClick()
                            coroutineScope.launch { gameViewModel.kickPlayer(player) }
                            showOptions.value = false
                        }),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "KICK",
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }
}