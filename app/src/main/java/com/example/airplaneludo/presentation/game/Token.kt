package com.example.airplaneludo.presentation.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.airplaneludo.ui.theme.Blue3
import com.example.airplaneludo.ui.theme.Red3
import com.example.shared.data.Team

@Composable
fun StackedToken(
    modifier: Modifier = Modifier,
    redCount: Int,
    blueCount: Int,
    moveToken: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .clickable(onClick = {
                moveToken()
            }), contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.offset((-7).dp),
            imageVector = Icons.Filled.Circle,
            contentDescription = "token",
            tint = Red3
        )
        if (redCount > 1) Text(modifier = Modifier.offset((-7).dp), text = "$redCount")
        Icon(
            modifier = Modifier.offset(7.dp),
            imageVector = Icons.Filled.Circle,
            contentDescription = "token",
            tint = Blue3
        )
        if (blueCount > 1) Text(modifier = Modifier.offset(7.dp), text = "$blueCount")
    }
}

@Composable
fun Token(
    modifier: Modifier = Modifier,
    team: Team,
    count: Int,
    moveToken: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)),
        exit = fadeOut(animationSpec = tween(durationMillis = 300))
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable(onClick = { moveToken() }),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Circle,
                contentDescription = "token",
                tint = if (team == Team.RED) Red3 else Blue3
            )
            if (count > 1) {
                Text(text = "$count", color = Color.White)
            }
        }
    }
}