package com.frozenfussion.snakegame.game

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SnakeEngineTest {
    private fun engine() = SnakeEngine(cols = 16, rows = 22, random = Random(1))

    @Test
    fun startsWithThreeCellsHeadingRight() {
        val e = engine()
        assertEquals(3, e.snake.size)
        assertEquals(Direction.RIGHT, e.direction)
        assertEquals(0, e.score)
        assertFalse(e.food in e.snake)
    }

    @Test
    fun movesOneCellPerStep() {
        val e = engine()
        val head = e.snake.first()
        // Make sure food is not in the way for this test.
        if (e.food == Cell(head.x + 1, head.y)) e.reset()
        val result = e.step()
        if (result == StepResult.MOVED) {
            assertEquals(Cell(head.x + 1, head.y), e.snake.first())
            assertEquals(3, e.snake.size)
        }
    }

    @Test
    fun wrapsAroundTheRightEdge() {
        val e = SnakeEngine(cols = 6, rows = 6, random = Random(2))
        // Head starts at x = 3; keep stepping right until it passes the edge.
        repeat(3) { e.step() }
        assertEquals(0, e.snake.first().x)
    }

    @Test
    fun wrapsAroundTheTopEdge() {
        val e = SnakeEngine(cols = 6, rows = 6, random = Random(3))
        e.steer(Direction.UP)
        repeat(4) { e.step() }
        assertEquals(5, e.snake.first().y)
    }

    @Test
    fun cannotReverseIntoItself() {
        val e = engine()
        e.steer(Direction.LEFT)
        e.step()
        assertEquals(Direction.RIGHT, e.direction)
    }

    @Test
    fun turnLeftAndRightAreRelativeToHeading() {
        val e = engine()
        e.turnLeft()
        e.step()
        assertEquals(Direction.UP, e.direction)
        e.turnRight()
        e.step()
        assertEquals(Direction.RIGHT, e.direction)
    }

    @Test
    fun eatingGrowsTheSnakeAndScores() {
        val e = engine()
        // Walk the snake in a loop until it finds food; wrap-around guarantees it will pass every row.
        var ate = false
        repeat(2000) {
            if (!ate) {
                when (e.step()) {
                    StepResult.ATE -> ate = true
                    StepResult.DIED -> e.reset()
                    StepResult.MOVED -> {
                        // Aim at the food with simple steering so the test is quick.
                        val head = e.snake.first()
                        when {
                            head.y != e.food.y && e.direction.dy == 0 ->
                                e.steer(if (e.food.y < head.y) Direction.UP else Direction.DOWN)
                            head.x != e.food.x && e.direction.dx == 0 ->
                                e.steer(if (e.food.x < head.x) Direction.LEFT else Direction.RIGHT)
                        }
                    }
                }
            }
        }
        assertTrue("snake should have eaten at least once", ate)
        assertEquals(10, e.score)
        assertEquals(4, e.snake.size)
        assertFalse(e.food in e.snake)
    }

    @Test
    fun runningIntoYourOwnBodyEndsTheGame() {
        val e = SnakeEngine(cols = 20, rows = 20, random = Random(5))
        // Grow the snake by letting it eat until it is long enough to bite itself.
        var guard = 0
        while (e.snake.size < 6 && guard++ < 5000) {
            val head = e.snake.first()
            when {
                head.y != e.food.y && e.direction.dy == 0 ->
                    e.steer(if (e.food.y < head.y) Direction.UP else Direction.DOWN)
                head.x != e.food.x && e.direction.dx == 0 ->
                    e.steer(if (e.food.x < head.x) Direction.LEFT else Direction.RIGHT)
            }
            if (e.step() == StepResult.DIED) e.reset()
        }
        assertTrue(e.snake.size >= 6)
        // A tight clockwise loop: right, down, left, up bites the body.
        e.steer(Direction.RIGHT); e.step()
        e.turnRight(); e.step()
        e.turnRight(); e.step()
        e.turnRight()
        assertEquals(StepResult.DIED, e.step())
    }
}
