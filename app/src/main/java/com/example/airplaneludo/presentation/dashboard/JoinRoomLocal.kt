package com.example.airplaneludo.presentation.dashboard

import android.net.nsd.NsdServiceInfo
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.navigation.NavController
import com.example.airplaneludo.presentation.GameViewModel
import com.example.airplaneludo.presentation.Screen
import com.example.airplaneludo.ui.theme.Blue3
import com.example.airplaneludo.ui.theme.Red3

@Composable
fun JoinRoomLocal(
    modifier: Modifier = Modifier,
    navController: NavController,
    gameViewModel: GameViewModel
) {
    LifecycleStartEffect(Unit) {
        gameViewModel.startLookingForRooms()
        onStopOrDispose {
            gameViewModel.stopNetworkDiscovery()
        }
    }
    val infiniteTransition = rememberInfiniteTransition(label = "IconColorTransition")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = Red3,
        targetValue = Blue3,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IconColorAnimation"
    )
    Box(
        modifier = modifier
            .padding(horizontal = 32.dp, vertical = 64.dp)
            .shadow(24.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1E1E2C).copy(alpha = 0.95f))
            .border(2.dp, animatedColor, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Cancel join room",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            gameViewModel.gameAudioManager.playClick()
                            gameViewModel.showJoinRoomLocal.value = false
                        }
                        .padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "SCANNING FOR GAMES IN LOCAL NETWORK...",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(color = Color.White)
            Spacer(modifier = Modifier.height(24.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(gameViewModel.availableNetworkRooms.size) { index ->
                    AvailableRoom(modifier = Modifier, selectRoom = { hostIp, selectedRoomId ->
                        gameViewModel.hostIp.value = hostIp
                        gameViewModel.roomId.value = selectedRoomId
                    }, gameViewModel, gameViewModel.availableNetworkRooms[index])
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    gameViewModel.gameAudioManager.playClick()
                    navController.navigate(Screen.Lobby.route)
                    gameViewModel.connectToNetworkRoom(
                        gameViewModel.hostIp.value,
                        gameViewModel.roomId.value!!
                    )
                    gameViewModel.showLobbyInfo.value = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(12.dp, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                enabled = gameViewModel.roomId.value != null
            ) {
                Text(
                    text = "JOIN ROOM",
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

@Composable
fun AvailableRoom(
    modifier: Modifier = Modifier,
    selectRoom: (String, Int) -> Unit,
    gameViewModel: GameViewModel,
    nsdServiceInfo: NsdServiceInfo
) {
    val rawName = nsdServiceInfo.serviceName
    val parsedHostName = rawName.substringAfter("_By_").replace("\\", "")
    val parsedRoomId =
        rawName.substringAfter("LudoRoom_").substringBefore("_By_").toIntOrNull() ?: 0
    val hostIp = nsdServiceInfo.host.hostAddress ?: ""
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(if (gameViewModel.roomId.value == parsedRoomId) Color.White else Color.Transparent)
            .border(2.dp, Color.White, RoundedCornerShape(32.dp))
            .clickable(onClick = {
                gameViewModel.gameAudioManager.playClick()
                selectRoom(hostIp, parsedRoomId)
            })
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${parsedHostName.uppercase()}'s ROOM (ID: $parsedRoomId)",
            color = if (gameViewModel.roomId.value == parsedRoomId) Color(0xFF1E1E2C).copy(alpha = 0.95f) else Color.White,
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
    }
}