package com.frozenfussion.snakegame.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ChiptuneSynthTest {
    @Test
    fun midiNumberSixtyNineIsConcertA() {
        assertEquals(440.0, ChiptuneSynth.midiToHz(69), 0.001)
    }

    @Test
    fun anOctaveUpDoublesTheFrequency() {
        assertEquals(ChiptuneSynth.midiToHz(60) * 2, ChiptuneSynth.midiToHz(72), 0.001)
    }

    @Test
    fun renderedLengthMatchesTheNoteLengths() {
        val samples = ChiptuneSynth.render(listOf(Note(69, 100), Note(-1, 50)))
        assertEquals(ChiptuneSynth.SAMPLE_RATE * 150 / 1000, samples.size)
    }

    @Test
    fun aRestIsSilent() {
        val samples = ChiptuneSynth.render(listOf(Note(-1, 20)))
        assertTrue(samples.all { it == 0.toShort() })
    }

    @Test
    fun aNoteMakesSoundButNeverClips() {
        val samples = ChiptuneSynth.render(listOf(Note(69, 100, Wave.SQUARE, 0.4f)))
        val peak = samples.maxOf { abs(it.toInt()) }
        assertTrue(peak > 1000)
        assertTrue(peak <= (0.4f * Short.MAX_VALUE).toInt())
    }

    @Test
    fun everySoundEffectRenders() {
        Sound.entries.forEach { assertTrue(ChiptuneSynth.render(it.notes).isNotEmpty()) }
    }
}
