package com.frozenfussion.snakegame.ui

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
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frozenfussion.snakegame.game.GameUiState

@Composable
fun GameOverScreen(state: GameUiState, onTryAgain: () -> Unit, onMenu: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        // The final board, dimmed, so you can see what you ran into.
        BoardView(state, Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 80.dp), faded = true)

        Column(
            Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(48.dp))
            BasicText(
                text = "GAME\nOVER",
                style = TextStyle(
                    fontFamily = PixelFont,
                    fontSize = 40.sp,
                    lineHeight = 52.sp,
                    color = Arcade.Magenta,
                    textAlign = TextAlign.Center,
                    shadow = Shadow(Color(0xFF7A1442), Offset(8f, 8f), 0f),
                ),
            )
            Spacer(Modifier.height(40.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .border(3.dp, Arcade.Cyan)
                    .background(Arcade.Background.copy(alpha = 0.94f))
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                ScoreRow("SCORE", state.score, 12.sp, 28.sp, Arcade.Yellow)
                Box(Modifier.fillMaxWidth().height(2.dp).background(Arcade.Outline))
                ScoreRow("BEST", state.highScore, 12.sp, 20.sp, Arcade.Cyan)
            }

            Spacer(Modifier.weight(1f))
            ArcadeButton("TAP TO\nTRY AGAIN", onTryAgain, height = 76.dp, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
            OutlineButton("MENU", onMenu)
        }
    }
}

@Composable
private fun ScoreRow(
    label: String,
    value: Int,
    labelSize: androidx.compose.ui.unit.TextUnit,
    valueSize: androidx.compose.ui.unit.TextUnit,
    valueColor: Color,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        PixelText(label, labelSize, Arcade.Dim)
        PixelText(value.toString().padStart(4, '0'), valueSize, valueColor)
    }
}
