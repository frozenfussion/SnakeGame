package com.frozenfussion.snakegame.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack

/** Renders every [Sound] once at start-up and replays it from memory on demand. */
class SfxPlayer {
    private val tracks: Map<Sound, AudioTrack?> = Sound.entries.associateWith { sound ->
        runCatching { createTrack(ChiptuneSynth.render(sound.notes)) }.getOrNull()
    }

    fun play(sound: Sound) {
        val track = tracks[sound] ?: return
        try {
            track.stop()
            track.reloadStaticData()
            track.play()
        } catch (_: IllegalStateException) {
            // Audio hardware busy or unavailable: the game simply continues silently.
        }
    }

    fun release() {
        tracks.values.forEach { it?.release() }
    }

    private fun createTrack(samples: ShortArray): AudioTrack {
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build(),
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(ChiptuneSynth.SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build(),
            )
            .setBufferSizeInBytes(samples.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        track.write(samples, 0, samples.size)
        return track
    }
}
