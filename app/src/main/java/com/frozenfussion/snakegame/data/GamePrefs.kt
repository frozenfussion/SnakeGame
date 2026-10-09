package com.frozenfussion.snakegame.data

import android.content.Context
import androidx.core.content.edit
import com.frozenfussion.snakegame.game.ControlMode

/** Saves the high score and the chosen control mode on the device. */
class GamePrefs(context: Context) {
    private val prefs = context.getSharedPreferences("snake_prefs", Context.MODE_PRIVATE)

    var highScore: Int
        get() = prefs.getInt(KEY_HIGH_SCORE, 0)
        set(value) = prefs.edit { putInt(KEY_HIGH_SCORE, value) }

    var controlMode: ControlMode
        get() = ControlMode.entries.firstOrNull { it.name == prefs.getString(KEY_MODE, null) } ?: ControlMode.TAP
        set(value) = prefs.edit { putString(KEY_MODE, value.name) }

    private companion object {
        const val KEY_HIGH_SCORE = "high_score"
        const val KEY_MODE = "control_mode"
    }
}
