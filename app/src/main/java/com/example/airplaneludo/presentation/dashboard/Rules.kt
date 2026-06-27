package com.example.airplaneludo.presentation.dashboard

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
import com.example.airplaneludo.presentation.GameViewModel
import com.example.airplaneludo.ui.theme.Blue3
import com.example.airplaneludo.ui.theme.Red3

@Composable
fun Rules(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel
) {
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
                    contentDescription = "Close Help",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            gameViewModel.gameAudioManager.playClick()
                            gameViewModel.showHelp.value = false
                        }
                        .padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "RULES",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
            )
            Spacer(modifier = Modifier.height(32.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = "OBJECTIVE: Be the first team to move all your tokens from the base to the home destination.",
                        color = Color.White,
                        textAlign = TextAlign.Justify
                    )
                }
                item {
                    Text(
                        text = "TEAM SETUP: The game consists of 2 teams (Red and Blue). Players join either team, and teams start with 4, 6, 8, or 12 tokens in their base.",
                        color = Color.White,
                        textAlign = TextAlign.Justify
                    )
                }
                item {
                    Text(
                        text = "TURN ORDER: Players roll in a strict alternating cycle based on their team (e.g., Red 1 ➔ Blue 1 ➔ Red 2 ➔ Blue 2).",
                        color = Color.White,
                        textAlign = TextAlign.Justify
                    )
                }
                item {
                    Text(
                        text = "ACTIVATION: A player must roll a '1' to activate and start contributing or moving their team's tokens.",
                        color = Color.White,
                        textAlign = TextAlign.Justify
                    )
                }
                item {
                    Text(
                        text = "DICE MECHANICS: Rolled using two 4-sided dice (0, 1, 2, 3). Rolling a (0, 0) results in a total value of 12.",
                        color = Color.White,
                        textAlign = TextAlign.Justify
                    )
                }
                item {
                    Text(
                        text = "CONTINUOUS TURNS: Rolling a 1, 5, 6, or 12 grants an extra roll. Rolled values accumulate in a pool until the turn is terminated by rolling a 2, 3, or 4.",
                        color = Color.White,
                        textAlign = TextAlign.Justify
                    )
                }
                item {
                    Text(
                        text = "GRANULAR MOVEMENT: Players can strategically distribute their accumulated pool of values across multiple tokens however they see fit.",
                        color = Color.White,
                        textAlign = TextAlign.Justify
                    )
                }
                item {
                    Text(
                        text = "CAPTURE BONUS: Capturing an opponent's token awards 1 additional bonus turn per capture for that round.",
                        color = Color.White,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }
    }
}
