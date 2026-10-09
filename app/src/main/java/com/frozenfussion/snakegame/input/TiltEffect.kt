package com.frozenfussion.snakegame.input

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.frozenfussion.snakegame.game.Direction

/** Listens to the accelerometer while [enabled] is true and reports where the player is tilting. */
@Composable
fun TiltEffect(enabled: Boolean, onDirection: (Direction) -> Unit) {
    val context = LocalContext.current
    val latestCallback by rememberUpdatedState(onDirection)

    DisposableEffect(enabled) {
        if (!enabled) return@DisposableEffect onDispose { }

        val manager = context.getSystemService(SensorManager::class.java)
        val sensor = manager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        // A fresh TiltSteering each time means "level" is whatever angle you hold the phone at right now.
        val steering = TiltSteering()
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                steering.update(event.values[0], event.values[1], event.values[2])?.let(latestCallback)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        if (manager != null && sensor != null) {
            manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
        onDispose { manager?.unregisterListener(listener) }
    }
}
