package com.frozenfussion.snakegame.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText
import com.frozenfussion.snakegame.game.ControlMode
import com.frozenfussion.snakegame.game.GameUiState

@Composable
fun StartScreen(
    state: GameUiState,
    tiltAvailable: Boolean,
    onSelectMode: (ControlMode) -> Unit,
    onPlay: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PixelText("HI-SCORE ${state.highScore.toString().padStart(4, '0')}", 10.sp, Arcade.Yellow, letterSpacing = 2.sp)

        Spacer(Modifier.height(32.dp))
        BasicText(
            text = "SNAKE",
            style = TextStyle(
                fontFamily = PixelFont,
                fontSize = 48.sp,
                color = Arcade.Green,
                shadow = Shadow(Color(0xFF0F6B3A), Offset(8f, 8f), 0f),
            ),
        )
        Spacer(Modifier.height(14.dp))
        PixelText("RETRO ARCADE", 10.sp, Arcade.Magenta, letterSpacing = 4.sp)

        Spacer(Modifier.height(28.dp))
        DecorSnake()

        Spacer(Modifier.height(32.dp))
        PixelText("CONTROLS", 10.sp, Arcade.Dim, letterSpacing = 2.sp)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ModeOption(
                label = "TAP",
                selected = state.mode == ControlMode.TAP,
                enabled = true,
                onClick = { onSelectMode(ControlMode.TAP) },
                modifier = Modifier.weight(1f),
            ) { color -> TapIcon(color) }
            ModeOption(
                label = "TILT",
                selected = state.mode == ControlMode.TILT,
                enabled = tiltAvailable,
                onClick = { onSelectMode(ControlMode.TILT) },
                modifier = Modifier.weight(1f),
            ) { color -> TiltIcon(color) }
        }
        Spacer(Modifier.height(12.dp))
        PixelText(
            text = when {
                state.mode == ControlMode.TILT -> "TILT YOUR PHONE TOWARD WHERE YOU WANT THE SNAKE TO GO."
                !tiltAvailable -> "TAP LEFT HALF TO TURN LEFT. TAP RIGHT HALF TO TURN RIGHT. (NO TILT SENSOR FOUND)"
                else -> "TAP LEFT HALF TO TURN LEFT. TAP RIGHT HALF TO TURN RIGHT."
            },
            size = 10.sp,
            color = Arcade.Dim,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
        )

        Spacer(Modifier.weight(1f))
        ArcadeButton("PLAY", onPlay, height = 68.dp, fontSize = 20.sp)
        Spacer(Modifier.height(22.dp))
        PixelText("WALLS WRAP AROUND", 10.sp, Arcade.Dim, letterSpacing = 1.sp)
    }
}

@Composable
private fun ModeOption(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (Color) -> Unit,
) {
    val color = if (selected) Arcade.Cyan else Arcade.Dim
    Column(
        modifier
            .heightIn(min = 112.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .border(3.dp, if (selected) Arcade.Cyan else Arcade.Outline)
            .background(if (selected) Arcade.Cyan.copy(alpha = 0.12f) else Color.Transparent)
            .semantics { this.selected = selected }
            .clickable(enabled = enabled, role = Role.RadioButton, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        icon(color)
        Spacer(Modifier.height(12.dp))
        PixelText(label, 14.sp, color)
    }
}

/** The little decorative snake from the design: a wiggle that is about to eat an apple. */
@Composable
private fun DecorSnake() {
    val path = listOf(
        0 to 2, 1 to 2, 1 to 1, 2 to 1, 3 to 1, 3 to 0, 4 to 0, 5 to 0, 5 to 1, 5 to 2,
        6 to 2, 7 to 2, 7 to 1, 8 to 1, 9 to 1, 9 to 0, 10 to 0, 11 to 0, 12 to 0,
    )
    Canvas(Modifier.size(330.dp, 66.dp)) {
        val cell = 22.dp.toPx()
        val inset = 1.dp.toPx()
        path.forEachIndexed { i, (cx, cy) ->
            val color = if (i == path.lastIndex) Arcade.GreenHead else Arcade.Green
            drawRect(color, Offset(cx * cell + inset, cy * cell + inset), Size(cell - inset * 2, cell - inset * 2))
        }
        drawRect(Arcade.Magenta, Offset(14 * cell + cell * 0.2f, cell * 0.2f), Size(cell * 0.6f, cell * 0.6f))
        drawRect(Arcade.Green, Offset(14 * cell + cell * 0.45f, cell * 0.05f), Size(cell * 0.18f, cell * 0.18f))
    }
}
