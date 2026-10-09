package com.frozenfussion.snakegame.input

import com.frozenfussion.snakegame.game.Direction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TiltSteeringTest {
    // smoothing = 1 means "no smoothing", so each sample counts fully in these tests.
    private fun steering() = TiltSteering(deadZoneDegrees = 12f, smoothing = 1f)

    // Phone held upright: gravity pulls along +y.
    private fun TiltSteering.holdUpright() = update(0f, 9.8f, 0f)

    @Test
    fun staysQuietInsideTheDeadZone() {
        val t = steering()
        t.holdUpright()
        assertNull(t.update(0.3f, 9.8f, 0.2f))
    }

    @Test
    fun rightEdgeDownSteersRight() {
        val t = steering()
        t.holdUpright()
        assertEquals(Direction.RIGHT, t.update(-3f, 9.3f, 0f))
    }

    @Test
    fun leftEdgeDownSteersLeft() {
        val t = steering()
        t.holdUpright()
        assertEquals(Direction.LEFT, t.update(3f, 9.3f, 0f))
    }

    @Test
    fun topEdgeLoweredSteersUp() {
        val t = steering()
        t.holdUpright()
        // Leaning the phone back so the screen faces the sky more.
        assertEquals(Direction.UP, t.update(0f, 7f, 6.8f))
    }

    @Test
    fun topEdgeRaisedSteersDown() {
        val t = steering()
        t.holdUpright()
        assertEquals(Direction.DOWN, t.update(0f, 7f, -6.8f))
    }

    @Test
    fun theAngleYouStartAtCountsAsLevel() {
        val t = steering()
        // Started tilted back at about 45 degrees.
        t.update(0f, 6.9f, 6.9f)
        assertNull(t.update(0f, 6.9f, 6.9f))
        // Tilting further back from there steers UP.
        assertEquals(Direction.UP, t.update(0f, 3f, 9.3f))
    }
}
