package com.frozenfussion.snakegame.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frozenfussion.snakegame.game.ControlMode
import com.frozenfussion.snakegame.game.Direction
import com.frozenfussion.snakegame.game.GameUiState
import com.frozenfussion.snakegame.game.Phase

@Composable
fun GameScreen(
    state: GameUiState,
    onTurnLeft: () -> Unit,
    onTurnRight: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onMenu: () -> Unit,
) {
    val tapMode = state.mode == ControlMode.TAP
    Box(
        Modifier
            .fillMaxSize()
            // In Tap mode the whole screen is the controller: left half turns left, right half turns right.
            .pointerInput(tapMode) {
                if (!tapMode) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown()
                    if (down.position.x < size.width / 2f) onTurnLeft() else onTurnRight()
                }
            },
    ) {
        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 16.dp)) {
            Hud(state, onPause)
            Spacer(Modifier.height(14.dp))
            BoardView(state, Modifier.weight(1f))
            Spacer(Modifier.height(16.dp))
            if (tapMode) TapHints() else TiltHints(state.direction)
        }

        if (state.phase == Phase.PAUSED) PauseOverlay(onResume, onMenu)
    }
}

@Composable
private fun Hud(state: GameUiState, onPause: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixelText("SCORE", 10.sp, Arcade.Dim)
                PixelText(state.score.toString().padStart(4, '0'), 20.sp, Arcade.Yellow)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PixelText("HI", 10.sp, Arcade.Dim)
                PixelText(state.highScore.toString().padStart(4, '0'), 20.sp, Arcade.Cyan)
            }
        }
        Box(
            Modifier
                .size(48.dp)
                .border(2.dp, Arcade.Cyan)
                .clickable(role = Role.Button, onClickLabel = "Pause", onClick = onPause),
            contentAlignment = Alignment.Center,
        ) {
            PauseIcon(Arcade.Cyan)
        }
    }
}

@Composable
private fun TapHints() {
    Column(Modifier.fillMaxWidth().height(170.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(Modifier.fillMaxWidth().height(22.dp), contentAlignment = Alignment.Center) {
            PixelText("TAP MODE", 10.sp, Arcade.Cyan, letterSpacing = 2.sp)
        }
        Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HintPanel(Arcade.Cyan, Modifier.weight(1f)) {
                TurnArrowIcon(left = true, color = Arcade.Cyan)
                Spacer(Modifier.height(12.dp))
                PixelText("TAP LEFT\nTURN LEFT", 10.sp, Arcade.Cyan, textAlign = TextAlign.Center, lineHeight = 18.sp)
            }
            HintPanel(Arcade.Magenta, Modifier.weight(1f)) {
                TurnArrowIcon(left = false, color = Arcade.Magenta)
                Spacer(Modifier.height(12.dp))
                PixelText("TAP RIGHT\nTURN RIGHT", 10.sp, Arcade.Magenta, textAlign = TextAlign.Center, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
private fun TiltHints(current: Direction) {
    Column(Modifier.fillMaxWidth().height(170.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(Modifier.fillMaxWidth().height(22.dp), contentAlignment = Alignment.Center) {
            PixelText("TILT MODE", 10.sp, Arcade.Cyan, letterSpacing = 2.sp)
        }
        HintPanel(Arcade.Cyan, Modifier.fillMaxWidth().weight(1f), horizontal = true) {
            TiltIcon(Arcade.Cyan, size = 56.dp, strokeWidth = 2.5f)
            Spacer(Modifier.size(24.dp))
            DirectionPad(current)
            Spacer(Modifier.size(24.dp))
            PixelText("TILT TO\nSTEER", 10.sp, Arcade.Cyan, lineHeight = 18.sp)
        }
    }
}

/** Shows the four directions and lights up the way the snake is currently heading. */
@Composable
private fun DirectionPad(current: Direction) {
    @Composable
    fun Key(direction: Direction?) {
        if (direction == null) {
            Spacer(Modifier.size(36.dp))
            return
        }
        val active = direction == current
        Box(
            Modifier
                .size(36.dp)
                .then(if (active) Modifier.background(Arcade.Cyan) else Modifier.border(2.dp, Arcade.Outline)),
            contentAlignment = Alignment.Center,
        ) {
            ChevronIcon(direction, if (active) Arcade.Background else Arcade.Dim)
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) { Key(null); Key(Direction.UP); Key(null) }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) { Key(Direction.LEFT); Key(null); Key(Direction.RIGHT) }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) { Key(null); Key(Direction.DOWN); Key(null) }
    }
}

@Composable
private fun HintPanel(
    color: Color,
    modifier: Modifier = Modifier,
    horizontal: Boolean = false,
    content: @Composable () -> Unit,
) {
    val panel = modifier
        .background(color.copy(alpha = 0.06f))
        .drawBehind {
            drawRect(
                color.copy(alpha = 0.55f),
                style = Stroke(2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))),
            )
        }
    if (horizontal) {
        Row(panel, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) { content() }
    } else {
        Column(panel, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { content() }
    }
}

@Composable
private fun PauseOverlay(onResume: () -> Unit, onMenu: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Arcade.Background.copy(alpha = 0.88f))
            // Swallow taps so they don't steer the snake while paused.
            .pointerInput(Unit) { awaitEachGesture { awaitFirstDown().consume() } },
        contentAlignment = Alignment.Center,
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            PixelText("PAUSED", 32.sp, Arcade.Cyan)
            Spacer(Modifier.height(48.dp))
            ArcadeButton("RESUME", onResume, height = 68.dp, fontSize = 18.sp)
            Spacer(Modifier.height(24.dp))
            OutlineButton("MENU", onMenu)
        }
    }
}
