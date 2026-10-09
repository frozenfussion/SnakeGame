package com.frozenfussion.snakegame.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/** Short buzzes for eating and for game over. */
class Haptics(context: Context) {
    private val vibrator: Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

    fun eat() = vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))

    fun gameOver() = vibrate(VibrationEffect.createWaveform(longArrayOf(0, 90, 60, 220), -1))

    private fun vibrate(effect: VibrationEffect) {
        if (vibrator?.hasVibrator() == true) vibrator.vibrate(effect)
    }
}
