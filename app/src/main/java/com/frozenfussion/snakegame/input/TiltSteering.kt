package com.frozenfussion.snakegame.input

import com.frozenfussion.snakegame.game.Direction
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.max

/**
 * Turns raw accelerometer readings into a steering direction.
 *
 * Think of a marble on the screen: the snake heads toward whichever edge is lowered.
 * The angle the phone is held at when the run starts counts as "level", so there is no
 * calibration screen. A small dead zone stops the snake twitching while you hold still.
 */
class TiltSteering(
    private val deadZoneDegrees: Float = 12f,
    private val smoothing: Float = 0.25f,
) {
    private var x = 0f
    private var y = 0f
    private var z = 0f
    private var hasReading = false

    private var baseRoll = 0f
    private var basePitch = 0f
    private var hasBase = false

    /** Feed one accelerometer sample (m/s²). Returns a direction once the phone is tilted past the dead zone. */
    fun update(ax: Float, ay: Float, az: Float): Direction? {
        if (!hasReading) {
            x = ax; y = ay; z = az
            hasReading = true
        } else {
            x += smoothing * (ax - x)
            y += smoothing * (ay - y)
            z += smoothing * (az - z)
        }

        // Roll: positive when the LEFT edge is lower. Pitch: 0 lying flat, 90 held upright.
        val roll = Math.toDegrees(atan2(x, hypot(y, z)).toDouble()).toFloat()
        val pitch = Math.toDegrees(atan2(y, z).toDouble()).toFloat()

        if (!hasBase) {
            baseRoll = roll
            basePitch = pitch
            hasBase = true
            return null
        }

        val dRoll = roll - baseRoll
        val dPitch = pitch - basePitch
        if (max(abs(dRoll), abs(dPitch)) < deadZoneDegrees) return null

        return if (abs(dRoll) >= abs(dPitch)) {
            if (dRoll < 0) Direction.RIGHT else Direction.LEFT
        } else {
            // Top edge lowered (phone leaning away) means pitch decreases.
            if (dPitch < 0) Direction.UP else Direction.DOWN
        }
    }
}
