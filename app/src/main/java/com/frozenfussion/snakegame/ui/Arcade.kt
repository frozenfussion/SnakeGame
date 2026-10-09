package com.frozenfussion.snakegame.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frozenfussion.snakegame.R
import com.frozenfussion.snakegame.game.Direction

/** The neon-on-dark palette from the approved design. */
object Arcade {
    val Background = Color(0xFF0B0B1E)
    val BoardBackground = Color(0xFF0E0E2A)
    val Grid = Color(0xFF828CFF).copy(alpha = 0.10f)
    val Cyan = Color(0xFF22E5FF)
    val Green = Color(0xFF39FF88)
    val GreenHead = Color(0xFFB8FFD6)
    val Magenta = Color(0xFFFF2E88)
    val MagentaShadow = Color(0xFF9A1A52)
    val Yellow = Color(0xFFFFE14D)
    val Dim = Color(0xFF9AA3E0)
    val Outline = Color(0xFF2B2F6B)
    val Text = Color(0xFFE8ECFF)
}

val PixelFont = FontFamily(Font(R.font.press_start_2p))

@Composable
fun PixelText(
    text: String,
    size: TextUnit,
    color: Color = Arcade.Text,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = TextStyle(
            fontFamily = PixelFont,
            fontSize = size,
            color = color,
            textAlign = textAlign,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight,
        ),
    )
}

/** A chunky button with a hard drop shadow, like the design's PLAY / TRY AGAIN buttons. */
@Composable
fun ArcadeButton(
    text: String,
    onClick: () -> Unit,
    height: Dp,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxWidth().height(height + 6.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(height)
                .drawBehind {
                    drawRect(Arcade.MagentaShadow, topLeft = Offset(0f, 6.dp.toPx()), size = size)
                }
                .background(Arcade.Magenta)
                .clickable(role = Role.Button, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            PixelText(
                text = text,
                size = fontSize,
                color = Arcade.Background,
                textAlign = TextAlign.Center,
                lineHeight = fontSize * 1.6f,
                letterSpacing = 1.sp,
            )
        }
    }
}

/** A flat outlined button for secondary actions such as MENU. */
@Composable
fun OutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(52.dp)
            .border(3.dp, Arcade.Outline)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        PixelText(text, 12.sp, Arcade.Dim, letterSpacing = 2.sp)
    }
}

/** Faint CRT scanlines drawn over everything. */
@Composable
fun Scanlines() {
    Canvas(Modifier.fillMaxSize()) {
        val gap = 3.dp.toPx()
        var y = 0f
        while (y < size.height) {
            drawRect(Color.Black.copy(alpha = 0.2f), topLeft = Offset(0f, y), size = Size(size.width, 1.dp.toPx()))
            y += gap
        }
    }
}

// ---- Small icons, drawn on a 40 x 40 grid so they match the design's SVGs ----

private fun DrawScope.polyline(color: Color, width: Float, grid: Float, vararg points: Pair<Float, Float>) {
    val scale = size.minDimension / grid
    for (i in 0 until points.size - 1) {
        drawLine(
            color = color,
            start = Offset(points[i].first * scale, points[i].second * scale),
            end = Offset(points[i + 1].first * scale, points[i + 1].second * scale),
            strokeWidth = width * scale,
            cap = StrokeCap.Square,
        )
    }
}

/** Corner arrow used on the Tap-mode hint panels. */
@Composable
fun TurnArrowIcon(left: Boolean, color: Color, size: Dp = 40.dp) {
    Canvas(Modifier.size(size)) {
        if (left) {
            polyline(color, 3f, 40f, 32f to 30f, 32f to 16f, 12f to 16f)
            polyline(color, 3f, 40f, 19f to 9f, 12f to 16f, 19f to 23f)
        } else {
            polyline(color, 3f, 40f, 8f to 30f, 8f to 16f, 28f to 16f)
            polyline(color, 3f, 40f, 21f to 9f, 28f to 16f, 21f to 23f)
        }
    }
}

/** Screen split in two with arrows: the Tap option icon. */
@Composable
fun TapIcon(color: Color, size: Dp = 40.dp) {
    Canvas(Modifier.size(size)) {
        val scale = this.size.minDimension / 40f
        drawRect(color, Offset(6f * scale, 6f * scale), Size(28f * scale, 28f * scale), style = Stroke(3f * scale))
        polyline(color, 3f, 40f, 20f to 6f, 20f to 34f)
        polyline(color, 3f, 40f, 16f to 16f, 12f to 20f, 16f to 24f)
        polyline(color, 3f, 40f, 24f to 16f, 28f to 20f, 24f to 24f)
    }
}

/** Tilted phone: the Tilt option icon. */
@Composable
fun TiltIcon(color: Color, size: Dp = 40.dp, strokeWidth: Float = 3f) {
    Canvas(Modifier.size(size)) {
        val scale = this.size.minDimension / 40f
        rotate(18f, pivot = Offset(20f * scale, 18f * scale)) {
            drawRect(color, Offset(12f * scale, 4f * scale), Size(16f * scale, 28f * scale), style = Stroke(strokeWidth * scale))
        }
        val arc = Path().apply {
            moveTo(6f * scale, 36f * scale)
            cubicTo(14f * scale, 40f * scale, 26f * scale, 40f * scale, 34f * scale, 36f * scale)
        }
        drawPath(arc, color, style = Stroke(strokeWidth * scale))
        polyline(color, strokeWidth, 40f, 6f to 36f, 6f to 30f)
        polyline(color, strokeWidth, 40f, 34f to 36f, 34f to 30f)
    }
}

/** A chevron pointing in [direction], drawn on a 20 x 20 grid. */
@Composable
fun ChevronIcon(direction: Direction, color: Color, size: Dp = 20.dp) {
    Canvas(Modifier.size(size)) {
        when (direction) {
            Direction.UP -> polyline(color, 3f, 20f, 4f to 13f, 10f to 7f, 16f to 13f)
            Direction.DOWN -> polyline(color, 3f, 20f, 4f to 7f, 10f to 13f, 16f to 7f)
            Direction.LEFT -> polyline(color, 3f, 20f, 13f to 4f, 7f to 10f, 13f to 16f)
            Direction.RIGHT -> polyline(color, 3f, 20f, 7f to 4f, 13f to 10f, 7f to 16f)
        }
    }
}

/** Two bars: the pause icon. */
@Composable
fun PauseIcon(color: Color, size: Dp = 20.dp) {
    Canvas(Modifier.size(size)) {
        val s = this.size.minDimension / 20f
        drawRect(color, Offset(4f * s, 3f * s), Size(4f * s, 14f * s))
        drawRect(color, Offset(12f * s, 3f * s), Size(4f * s, 14f * s))
    }
}
