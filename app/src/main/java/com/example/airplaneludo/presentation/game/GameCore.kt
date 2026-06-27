package com.example.airplaneludo.presentation.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.airplaneludo.presentation.GameViewModel
import com.example.airplaneludo.presentation.Screen
import com.example.airplaneludo.ui.theme.Blue3
import com.example.airplaneludo.ui.theme.Red3
import com.example.shared.data.Team

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCore(
    modifier: Modifier = Modifier,
    navController: NavController,
    gameViewModel: GameViewModel
) {
    gameViewModel.gameAudioManager.stopBackgroundMusic()
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(
                "${gameViewModel.currentPlayer.value.name}'s TURN",
                color = Color.White
            )
        }, navigationIcon = {
            Icon(
                imageVector = Icons.Filled.ExitToApp,
                contentDescription = "Leave game",
                tint = Color.White,
                modifier = Modifier
                    .clickable(onClick = {
                        gameViewModel.gameAudioManager.playClick()
                        gameViewModel.showExitRoom.value = true
                    })
                    .padding(4.dp)
            )
        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black))
    }) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            GameBoard(Modifier.fillMaxWidth(), gameViewModel)
            GridControl(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.Black
                    )
                    .padding(1.dp)
                    .weight(1f), gameViewModel = gameViewModel
            )
        }
        if (gameViewModel.gameOver.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                GameResult(modifier = Modifier, gameViewModel, navController)
            }
        }
        if (gameViewModel.showExitRoom.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                GameExit(
                    modifier = Modifier,
                    gameViewModel,
                    navController,
                    gameViewModel.exitRoomMessage.value
                )
            }
        }
    }
}

@Composable
fun GameResult(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel,
    navController: NavController
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = modifier
                .padding(horizontal = 32.dp, vertical = 64.dp)
                .shadow(24.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1E1E2C).copy(alpha = 0.95f))
                .border(
                    2.dp,
                    if (gameViewModel.winningTeam.value == Team.RED) Red3 else Blue3,
                    RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "GAME OVER",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = if (gameViewModel.winningTeam.value == Team.RED) "RED TEAM WON" else "BLUE TEAM WON",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        color = if (gameViewModel.winningTeam.value == Team.RED) Red3 else Blue3,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        gameViewModel.gameAudioManager.playClick()
                        navController.navigate(Screen.Lobby.route)
                        gameViewModel.gameOver.value = false
                        gameViewModel.winningTeam.value = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(12.dp, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EXIT",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp,
                                color = Color.Black
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameExit(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel,
    navController: NavController,
    message: String? = null
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = modifier
                .padding(horizontal = 32.dp, vertical = 64.dp)
                .shadow(24.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1E1E2C).copy(alpha = 0.95f))
                .border(2.dp, Color.White, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Cancel exit room",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                gameViewModel.gameAudioManager.playClick()
                                gameViewModel.showExitRoom.value = false
                            }
                            .padding(4.dp)
                    )
                }
                Text(
                    text = "ALERT",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = if (message != null) gameViewModel.exitRoomMessage.value!! else "LEAVE GAME?",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        color = if (gameViewModel.winningTeam.value == Team.RED) Red3 else Blue3,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        gameViewModel.gameAudioManager.playClick()
                        gameViewModel.showExitRoom.value = false
                        gameViewModel.gameStartIsPlaying.value = false
                        if (gameViewModel.player.value?.id == gameViewModel.host.value?.id)
                            gameViewModel.stopNetworkDiscovery()
                        gameViewModel.exitRoom()
                        navController.navigate(Screen.Dashboard.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(12.dp, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EXIT",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp,
                                color = Color.Black
                            )
                        )
                    }
                }
            }
        }
    }
}
