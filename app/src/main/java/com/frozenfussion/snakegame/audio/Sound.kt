package com.frozenfussion.snakegame.audio

/** Every sound effect in the game, written as notes. MIDI numbers: 60 = C4, 72 = C5, 84 = C6. */
enum class Sound(val notes: List<Note>) {
    START(
        listOf(Note(72, 70), Note(76, 70), Note(79, 70), Note(84, 140)),
    ),
    EAT(
        listOf(Note(76, 55), Note(83, 90)),
    ),
    GAME_OVER(
        listOf(
            Note(67, 150, Wave.TRIANGLE, 0.6f),
            Note(64, 150, Wave.TRIANGLE, 0.6f),
            Note(60, 150, Wave.TRIANGLE, 0.6f),
            Note(55, 380, Wave.TRIANGLE, 0.6f),
        ),
    ),
    CLICK(
        listOf(Note(81, 40)),
    ),
}
