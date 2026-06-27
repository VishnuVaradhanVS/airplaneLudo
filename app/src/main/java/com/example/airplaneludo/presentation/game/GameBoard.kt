package com.example.airplaneludo.presentation.game

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.airplaneludo.presentation.GameViewModel
import com.example.airplaneludo.ui.theme.Blue3
import com.example.airplaneludo.ui.theme.Blue4
import com.example.airplaneludo.ui.theme.Blue5
import com.example.airplaneludo.ui.theme.Blue6
import com.example.airplaneludo.ui.theme.Red3
import com.example.airplaneludo.ui.theme.Red4
import com.example.airplaneludo.ui.theme.Red5
import com.example.airplaneludo.ui.theme.Red6
import com.example.shared.data.GridPathCords
import com.example.shared.data.Team
import com.example.shared.data.Token
import com.example.shared.data.blueBase
import com.example.shared.data.bluePath
import com.example.shared.data.pathCords
import com.example.shared.data.redBase
import com.example.shared.data.redPath
import com.example.shared.data.safe
import kotlinx.coroutines.launch

@Composable
fun GameBoard(modifier: Modifier = Modifier, gameViewModel: GameViewModel) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(13f / 19f)
            .background(
                Color.Black
            )
            .padding(2.dp, 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            for (x in 0 until 19) {
                Row(modifier = Modifier.weight(1f)) {
                    for (y in 0 until 13) {
                        if (pathCords.contains(GridPathCords(x, y)))
                            Cell(modifier = Modifier.weight(1f), GridPathCords(x, y), gameViewModel)
                        else if (redBase.contains(GridPathCords(x, y)))
                            HomeCell(
                                modifier = Modifier.weight(1f),
                                GridPathCords(x, y),
                                gameViewModel.redBaseCount.value,
                                gameViewModel.redHomeCount.value,
                                gameViewModel
                            )
                        else if (blueBase.contains(GridPathCords(x, y)))
                            HomeCell(
                                modifier = Modifier.weight(1f),
                                GridPathCords(x, y),
                                gameViewModel.blueBaseCount.value,
                                gameViewModel.blueHomeCount.value,
                                gameViewModel
                            )
                        else
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeCell(
    modifier: Modifier = Modifier,
    cords: GridPathCords,
    base: Int,
    home: Int,
    gameViewModel: GameViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                if (cords.x == 14 && (cords.y == 2 || cords.y == 3)) {
                    if (gameViewModel.redHomeEnabled.value) Red5 else Color.DarkGray
                } else if (cords.x == 14 && (cords.y === 9 || cords.y == 10)) {
                    if (gameViewModel.blueHomeEnabled.value) Blue5 else Color.DarkGray
                } else if (cords.y < 6) Red5 else Blue5
            ),
        contentAlignment = Alignment.Center
    ) {
        if (cords.y == 2) {
            if (cords.x == 14) {
                Icon(imageVector = Icons.Filled.Home, contentDescription = "Red home", tint = Red3)
            } else {
                Icon(
                    imageVector = Icons.Filled.AddBox,
                    contentDescription = "Red base",
                    tint = Red3,
                    modifier = Modifier.clickable(onClick = {
                        coroutineScope.launch {
                            if ((gameViewModel.selectedStep.value == 1 || gameViewModel.selectedStep.value == 5) && gameViewModel.player.value?.team == Team.RED) {
                                gameViewModel.spawnToken()
                            }
                        }
                    })
                )
            }
        }
        if (cords.y == 3) {
            if (cords.x == 14) {
                Text(text = "$home", color = Red3)
            } else {
                Text(text = "$base", color = Red3)
            }
        }

        if (cords.y == 9) {
            if (cords.x == 14) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Blue home",
                    tint = Blue3
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.AddBox,
                    contentDescription = "Blue base",
                    tint = Blue3,
                    modifier = Modifier.clickable(onClick = {
                        coroutineScope.launch {
                            if ((gameViewModel.selectedStep.value == 1 || gameViewModel.selectedStep.value == 5) && gameViewModel.player.value?.team == Team.BLUE) {
                                gameViewModel.spawnToken()
                            }
                        }
                    })
                )
            }
        }
        if (cords.y == 10) {
            if (cords.x == 14) {
                Text(text = "$home", color = Blue3)
            } else {
                Text(text = "$base", color = Blue3)
            }
        }
    }
}

@Composable
fun Cell(modifier: Modifier = Modifier, cords: GridPathCords, gameViewModel: GameViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val animatedIconColorRed by rememberInfiniteTransition().animateColor(
        initialValue = Red4,
        targetValue = Red6,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IconColorAnimation"
    )
    val animatedIconColorBlue by rememberInfiniteTransition().animateColor(
        initialValue = Blue4,
        targetValue = Blue6,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IconColorAnimation"
    )
    val containsToken = remember { mutableStateOf(false) }
    val containsTokenRed = remember { mutableStateOf(false) }
    val containsTokenBlue = remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .border(1.dp, Color.Black)
            .background(
                if (gameViewModel.tokenInMotion.value) {
                    Color.White
                } else {
                    if (containsToken.value && gameViewModel.selectedStep.value != 0) {
                        if (gameViewModel.killedTokenRed.value || containsTokenRed.value && gameViewModel.player.value?.team == Team.RED) animatedIconColorRed
                        else if (gameViewModel.killedTokenBlue.value || containsTokenBlue.value && gameViewModel.player.value?.team == Team.BLUE) animatedIconColorBlue
                        else Color.White
                    } else if (gameViewModel.redHome.value) Red4
                    else if (gameViewModel.blueHome.value) Blue4
                    else Color.White
                }
            ), contentAlignment = Alignment.Center
    ) {
        if (safe.contains(cords)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                drawLine(
                    start = Offset(x = 0f, y = 0f),
                    end = Offset(x = width, y = height),
                    color = Color.Black,
                    strokeWidth = 4f
                )
                drawLine(
                    start = Offset(x = width, y = 0f),
                    end = Offset(x = 0f, y = height),
                    color = Color.Black,
                    strokeWidth = 4f
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            if (cords.x == 2 && cords.y == 6) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = if (gameViewModel.player.value?.team == Team.RED) Red3 else Blue3
                )
            }
            var redTokens = mutableListOf<Token>()
            for (token in gameViewModel.redTokens) {
                if (token.position == -1) {
                    continue
                }
                if (cords == redPath[token.position]) {
                    redTokens.add(token)
                }
            }
            var blueTokens = mutableListOf<Token>()
            for (token in gameViewModel.blueTokens) {
                if (token.position == -1) {
                    continue
                }
                if (cords == bluePath[token.position]) {
                    blueTokens.add(token)
                }
            }
            if (redTokens.isNotEmpty() && blueTokens.isNotEmpty()) {
                containsToken.value = true
                containsTokenRed.value = true
                containsTokenBlue.value = true
                StackedToken(redCount = redTokens.size, blueCount = blueTokens.size, moveToken = {
                    coroutineScope.launch {
                        if (gameViewModel.player.value == gameViewModel.currentPlayer.value) {
                            if (gameViewModel.player.value?.team == Team.RED) {
                                val movableToken = redTokens.get(redTokens.size - 1)
                                if (movableToken.position + gameViewModel.selectedStep.value <= redPath.size - 1) {
                                    gameViewModel.moveToken(
                                        redTokens.get(redTokens.size - 1),
                                        Team.RED
                                    )
                                }
                            } else {
                                val movableToken = blueTokens.get(blueTokens.size - 1)
                                if (movableToken.position + gameViewModel.selectedStep.value <= bluePath.size - 1) {
                                    gameViewModel.moveToken(
                                        blueTokens.get(blueTokens.size - 1),
                                        Team.BLUE
                                    )
                                }
                            }
                        }
                    }
                })
            } else if (redTokens.isNotEmpty()) {
                containsToken.value = true
                containsTokenRed.value = true
                containsTokenBlue.value = false
                Token(team = Team.RED, count = redTokens.size, moveToken = {
                    coroutineScope.launch {
                        if (gameViewModel.player.value == gameViewModel.currentPlayer.value && gameViewModel.currentPlayer.value.team == Team.RED) {
                            val movableToken = redTokens.get(redTokens.size - 1)
                            if (movableToken.position + gameViewModel.selectedStep.value <= redPath.size - 1) {
                                gameViewModel.moveToken(redTokens.get(redTokens.size - 1), Team.RED)
                            }
                        }
                    }
                })
            } else if (blueTokens.isNotEmpty()) {
                containsToken.value = true
                containsTokenRed.value = false
                containsTokenBlue.value = true
                Token(team = Team.BLUE, count = blueTokens.size, moveToken = {
                    coroutineScope.launch {
                        if (gameViewModel.player.value == gameViewModel.currentPlayer.value && gameViewModel.currentPlayer.value.team == Team.BLUE) {
                            val movableToken = blueTokens.get(blueTokens.size - 1)
                            if (movableToken.position + gameViewModel.selectedStep.value <= bluePath.size - 1) {
                                gameViewModel.moveToken(
                                    blueTokens.get(blueTokens.size - 1),
                                    Team.BLUE
                                )
                            }
                        }
                    }
                })
            } else {
                containsToken.value = false
                containsTokenRed.value = false
                containsTokenBlue.value = false
            }
        }
    }
}