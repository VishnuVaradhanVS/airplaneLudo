package com.example.airplaneludo.presentation.game

import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.airplaneludo.presentation.GameViewModel
import com.example.airplaneludo.ui.theme.Blue2
import com.example.airplaneludo.ui.theme.Blue5
import com.example.airplaneludo.ui.theme.Red2
import com.example.airplaneludo.ui.theme.Red5
import com.example.shared.data.Team
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun Dice(modifier: Modifier = Modifier, gameViewModel: GameViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var shuffleValue1 by remember { mutableIntStateOf(1) }
    var shuffleValue2 by remember { mutableIntStateOf(1) }
    val serverDice1 by gameViewModel.dice1
    val serverDice2 by gameViewModel.dice2
    val triggerDiceRoll by gameViewModel.triggerDiceRoll
    val sharedRotationX = remember { Animatable(0f) }
    var isRolling by remember { mutableStateOf(false) }
    val displayValue1 = if (isRolling) shuffleValue1 else serverDice1
    val displayValue2 = if (isRolling) shuffleValue2 else serverDice2
    LaunchedEffect(triggerDiceRoll) {
        isRolling = true
        gameViewModel.gameAudioManager.playDice()
        val shuffleJob = launch {
            val duration = 800
            val step = 60
            var elapsed = 0
            while (elapsed < duration) {
                shuffleValue1 = Random.nextInt(0, 4)
                shuffleValue2 = Random.nextInt(0, 4)
                delay(step.toLong())
                elapsed += step
            }
        }
        sharedRotationX.snapTo(0f)
        sharedRotationX.animateTo(
            targetValue = 720f,
            animationSpec = tween(durationMillis = 800)
        )
        shuffleJob.join()
        isRolling = false
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 0.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DICE(
                modifier = modifier,
                value = displayValue1,
                rotationX = sharedRotationX.value,
                onDiceClick = {
                    if (!isRolling) {
                        coroutineScope.launch {
                            if (gameViewModel.canRollDice.value) {
                                gameViewModel.rollDice()
                            }
                        }
                    }
                },
                gameViewModel = gameViewModel
            )
            Spacer(modifier = Modifier.height(4.dp))
            DICE(
                modifier = modifier,
                value = displayValue2,
                rotationX = sharedRotationX.value,
                onDiceClick = {
                    if (!isRolling) {
                        coroutineScope.launch {
                            if (gameViewModel.canRollDice.value) {
                                gameViewModel.rollDice()
                            }
                        }
                    }
                },
                gameViewModel = gameViewModel
            )
        }
    }
}

@Composable
fun DICE(
    modifier: Modifier = Modifier,
    value: Int,
    rotationX: Float,
    onDiceClick: () -> Unit,
    gameViewModel: GameViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .graphicsLayer {
                this.rotationX = rotationX
                cameraDistance = 16f * density
            }
            .background(if (gameViewModel.currentPlayer.value?.team == Team.RED) Red5 else Blue5)
            .border(2.dp, if (gameViewModel.currentPlayer.value?.team == Team.RED) Red2 else Blue2)
            .clickable { onDiceClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val dotTint = if (gameViewModel.currentPlayer.value?.team == Team.RED) Red2 else Blue2
            if (value in 1..6) {
                repeat(value) {
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        contentDescription = "Dice Dot",
                        tint = dotTint
                    )
                }
            } else {
                Text(text = if (value > 0) "$value" else "", color = dotTint)
            }
        }
    }
}