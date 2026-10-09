package com.frozenfussion.snakegame.game

import kotlin.random.Random

enum class StepResult { MOVED, ATE, DIED }

/**
 * The rules of Snake, with no Android code in it so it can be unit tested.
 *
 * The board wraps around: leaving one edge brings the snake back on the opposite edge.
 * The only way to die is to run into your own body.
 */
class SnakeEngine(
    val cols: Int,
    val rows: Int,
    private val random: Random = Random.Default,
) {
    // Head first. The last element is the tail.
    private val body = ArrayDeque<Cell>()
    private var pendingTurn: Direction? = null

    val snake: List<Cell> get() = body.toList()
    var food: Cell = Cell(0, 0)
        private set
    var direction: Direction = Direction.RIGHT
        private set
    var score: Int = 0
        private set

    init {
        reset()
    }

    fun reset() {
        body.clear()
        val headX = cols / 2
        val headY = rows / 2
        for (i in 0 until INITIAL_LENGTH) body.addLast(Cell(headX - i, headY))
        direction = Direction.RIGHT
        pendingTurn = null
        score = 0
        food = randomFreeCell() ?: Cell(0, 0)
    }

    /** Tap mode: turn 90 degrees to the left of the current heading. */
    fun turnLeft() {
        pendingTurn = direction.turnLeft()
    }

    /** Tap mode: turn 90 degrees to the right of the current heading. */
    fun turnRight() {
        pendingTurn = direction.turnRight()
    }

    /** Tilt mode: head in an absolute direction. Reversing straight into the neck is ignored. */
    fun steer(target: Direction) {
        if (target != direction.opposite) pendingTurn = target
    }

    /** Advance the game by one tick. */
    fun step(): StepResult {
        pendingTurn?.let { direction = it }
        pendingTurn = null

        val head = body.first()
        val next = Cell(
            x = Math.floorMod(head.x + direction.dx, cols),
            y = Math.floorMod(head.y + direction.dy, rows),
        )

        // The tail moves out of the way this tick, so stepping onto it is safe.
        if (next in body && next != body.last()) return StepResult.DIED

        body.addFirst(next)
        if (next != food) {
            body.removeLast()
            return StepResult.MOVED
        }

        score += POINTS_PER_FOOD
        // Board completely full: nothing left to eat, so the run ends.
        food = randomFreeCell() ?: return StepResult.DIED
        return StepResult.ATE
    }

    private fun randomFreeCell(): Cell? {
        val occupied = body.toSet()
        val free = buildList {
            for (y in 0 until rows) for (x in 0 until cols) {
                val cell = Cell(x, y)
                if (cell !in occupied) add(cell)
            }
        }
        return if (free.isEmpty()) null else free[random.nextInt(free.size)]
    }

    companion object {
        const val INITIAL_LENGTH = 3
        const val POINTS_PER_FOOD = 10
    }
}
