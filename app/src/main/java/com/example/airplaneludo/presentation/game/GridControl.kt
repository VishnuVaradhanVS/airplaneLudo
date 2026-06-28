package com.example.airplaneludo.presentation.game

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airplaneludo.presentation.GameViewModel
import com.example.airplaneludo.ui.theme.Blue3
import com.example.airplaneludo.ui.theme.Blue5
import com.example.airplaneludo.ui.theme.Blue6
import com.example.airplaneludo.ui.theme.Red3
import com.example.airplaneludo.ui.theme.Red5
import com.example.airplaneludo.ui.theme.Red6
import com.example.shared.data.Team
import com.example.shared.data.Token

@Composable
fun GridControl(modifier: Modifier = Modifier, gameViewModel: GameViewModel) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DiceCount(
                modifier = modifier
                    .weight(1f)
                    .background(Color.Transparent), gameViewModel
            )
            Spacer(modifier = Modifier.height(4.dp))
            Dice(modifier = modifier, gameViewModel = gameViewModel)
        }
    }
}

@Composable
fun DiceCount(modifier: Modifier = Modifier, gameViewModel: GameViewModel) {
    val diceDenomination = gameViewModel.diceDenomination
    val currentSelected = gameViewModel.selectedStep
    val animatedIconColor by rememberInfiniteTransition().animateColor(
        initialValue = if (gameViewModel.currentPlayer.value?.team == Team.RED) Blue6 else Red6,
        targetValue = if (gameViewModel.currentPlayer.value?.team == Team.RED) Blue3 else Red3,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IconColorAnimation"
    )
    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            for ((key, value) in gameViewModel.diceDenomination) {
                UnitCount(
                    modifier = modifier
                        .weight(1f)
                        .border(
                            4.dp,
                            if (currentSelected.value == key) animatedIconColor else Color.Transparent,
                            RoundedCornerShape(32.dp)
                        )
                        .padding(2.dp), key, value, gameViewModel, selectedCount = {
                        gameViewModel.gameAudioManager.playClick()
                        gameViewModel.selectedStep.value = key
                    })
            }

        }

    }
}

@Composable
fun UnitCount(
    modifier: Modifier = Modifier,
    value: Int,
    count: Int,
    gameViewModel: GameViewModel,
    selectedCount: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .background(Color.Transparent)
            .border(2.dp, Color.White, RoundedCornerShape(32.dp))
            .padding(2.dp)
            .clickable(onClick = {
                if (gameViewModel.canMoveToken.value) {
                    selectedCount()
                }
            }),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(Color.Transparent)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Box(
                modifier = Modifier
                    .background(if (gameViewModel.currentPlayer.value?.team == Team.RED) Red3 else Blue3)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${value}",
                    color = if (gameViewModel.currentPlayer.value?.team == Team.RED) Red5 else Blue5,
                    fontSize = 32.sp
                )
                if (count > 1) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.End
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .scale(1.1f)
                                .padding(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Circle,
                                contentDescription = "token",
                                tint = if (gameViewModel.currentPlayer.value?.team == Team.RED) Red5 else Blue5
                            )
                            Text(
                                text = "$count",
                                color = if (gameViewModel.currentPlayer.value?.team == Team.RED) Red3 else Blue3
                            )
                        }
                    }
                }
            }
        }
    }
}