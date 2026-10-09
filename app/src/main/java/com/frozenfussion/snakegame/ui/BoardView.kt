package com.frozenfussion.snakegame.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.frozenfussion.snakegame.game.Direction
import com.frozenfussion.snakegame.game.GameUiState

/**
 * The playing field. Fills the space it is given and picks the largest cell size that fits,
 * so the board looks right on small and tall phones alike.
 */
@Composable
fun BoardView(state: GameUiState, modifier: Modifier = Modifier, faded: Boolean = false) {
    BoxWithConstraints(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        val border = 3.dp
        val cell = minOf((maxWidth - border * 2) / state.cols, (maxHeight - border * 2) / state.rows)
        Canvas(
            Modifier
                .size(cell * state.cols + border * 2, cell * state.rows + border * 2)
                .alpha(if (faded) 0.22f else 1f)
                .border(border, Arcade.Cyan)
                .background(Arcade.BoardBackground)
        ) {
            drawBoard(state, cell.toPx())
        }
    }
}

private fun DrawScope.drawBoard(state: GameUiState, cell: Float) {
    // Grid lines
    for (x in 1 until state.cols) {
        drawRect(Arcade.Grid, Offset(x * cell, 0f), Size(1f, size.height))
    }
    for (y in 1 until state.rows) {
        drawRect(Arcade.Grid, Offset(0f, y * cell), Size(size.width, 1f))
    }

    // Food: a magenta apple with a green stem
    val fx = state.food.x * cell
    val fy = state.food.y * cell
    drawRect(Arcade.Magenta.copy(alpha = 0.25f), Offset(fx + cell * 0.1f, fy + cell * 0.1f), Size(cell * 0.8f, cell * 0.8f))
    drawRect(Arcade.Magenta, Offset(fx + cell * 0.2f, fy + cell * 0.2f), Size(cell * 0.6f, cell * 0.6f))
    drawRect(Arcade.Green, Offset(fx + cell * 0.45f, fy + cell * 0.05f), Size(cell * 0.18f, cell * 0.18f))

    // Snake body, then the head on top
    val inset = cell * 0.05f
    state.snake.forEachIndexed { index, segment ->
        val topLeft = Offset(segment.x * cell + inset, segment.y * cell + inset)
        val square = Size(cell - inset * 2, cell - inset * 2)
        val isHead = index == 0
        val color = if (isHead) Arcade.GreenHead else Arcade.Green
        drawRect(color.copy(alpha = 0.22f), topLeft - Offset(inset, inset), Size(cell, cell))
        drawRect(color, topLeft, square)
        if (isHead) drawEyes(segment.x * cell, segment.y * cell, cell, state.direction)
    }
}

/** Two small dark squares on the side of the head that faces the way the snake is going. */
private fun DrawScope.drawEyes(left: Float, top: Float, cell: Float, direction: Direction) {
    val eye = cell * 0.2f
    val front = cell * 0.62f
    val back = cell * 0.18f
    val sideA = cell * 0.18f
    val sideB = cell * 0.62f
    val ink = Color(0xFF0B0B1E)
    val size = Size(eye, eye)
    when (direction) {
        Direction.RIGHT -> {
            drawRect(ink, Offset(left + front, top + sideA), size)
            drawRect(ink, Offset(left + front, top + sideB), size)
        }
        Direction.LEFT -> {
            drawRect(ink, Offset(left + back, top + sideA), size)
            drawRect(ink, Offset(left + back, top + sideB), size)
        }
        Direction.UP -> {
            drawRect(ink, Offset(left + sideA, top + back), size)
            drawRect(ink, Offset(left + sideB, top + back), size)
        }
        Direction.DOWN -> {
            drawRect(ink, Offset(left + sideA, top + front), size)
            drawRect(ink, Offset(left + sideB, top + front), size)
        }
    }
}
