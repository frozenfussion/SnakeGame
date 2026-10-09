package com.frozenfussion.snakegame.game

/** The four ways the snake can move. Listed clockwise so turning is just "next" or "previous". */
enum class Direction(val dx: Int, val dy: Int) {
    UP(0, -1),
    RIGHT(1, 0),
    DOWN(0, 1),
    LEFT(-1, 0);

    val opposite: Direction
        get() = when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }

    fun turnLeft(): Direction = entries[(ordinal + 3) % 4]

    fun turnRight(): Direction = entries[(ordinal + 1) % 4]
}
