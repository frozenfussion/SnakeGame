package com.frozenfussion.snakegame.audio

import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow

enum class Wave { SQUARE, TRIANGLE }

/**
 * One note, described the way MIDI does it: a note number (60 = middle C, 69 = A440)
 * and a length. A negative number is a rest.
 */
data class Note(
    val midi: Int,
    val ms: Int,
    val wave: Wave = Wave.SQUARE,
    val volume: Float = 0.4f,
)

/** Turns a list of [Note]s into raw 16-bit audio samples, the way an old sound chip would. */
object ChiptuneSynth {
    const val SAMPLE_RATE = 22_050

    private const val ATTACK_MS = 2
    private const val RELEASE_MS = 8

    fun midiToHz(midi: Int): Double = 440.0 * 2.0.pow((midi - 69) / 12.0)

    fun render(notes: List<Note>): ShortArray {
        val total = notes.sumOf { samplesFor(it.ms) }
        val out = ShortArray(total)
        var pos = 0
        for (note in notes) {
            val count = samplesFor(note.ms)
            if (note.midi >= 0) {
                val step = midiToHz(note.midi) / SAMPLE_RATE
                val attack = samplesFor(ATTACK_MS)
                val release = samplesFor(RELEASE_MS)
                var phase = 0.0
                for (i in 0 until count) {
                    val raw = when (note.wave) {
                        Wave.SQUARE -> if (phase < 0.5) 1.0 else -1.0
                        Wave.TRIANGLE -> 4.0 * abs(phase - 0.5) - 1.0
                    }
                    // Short fade in and out so notes don't click, plus a gentle decay for a "blip" feel.
                    val fadeIn = min(1.0, i / attack.toDouble())
                    val fadeOut = min(1.0, (count - i) / release.toDouble())
                    val decay = 1.0 - 0.4 * i / count
                    out[pos + i] = (raw * note.volume * fadeIn * fadeOut * decay * Short.MAX_VALUE).toInt().toShort()
                    phase += step
                    if (phase >= 1.0) phase -= 1.0
                }
            }
            pos += count
        }
        return out
    }

    private fun samplesFor(ms: Int): Int = SAMPLE_RATE * ms / 1000
}
