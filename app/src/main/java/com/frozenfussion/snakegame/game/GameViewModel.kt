package com.frozenfussion.snakegame.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.frozenfussion.snakegame.data.GamePrefs
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class Phase { MENU, PLAYING, PAUSED, GAME_OVER }

enum class ControlMode { TAP, TILT }

/** Things that happened which the screen reacts to with sound and vibration. */
enum class GameEvent { START, EAT, GAME_OVER, CLICK }

data class GameUiState(
    val phase: Phase,
    val mode: ControlMode,
    val snake: List<Cell>,
    val food: Cell,
    val direction: Direction,
    val score: Int,
    val highScore: Int,
) {
    val cols: Int get() = BOARD_COLS
    val rows: Int get() = BOARD_ROWS
}

const val BOARD_COLS = 16
const val BOARD_ROWS = 22

/** Speed is constant: the snake moves one cell every tick. */
const val TICK_MS = 150L

/** Owns the engine, runs the game loop and exposes everything the UI needs as state. */
class GameViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = GamePrefs(app)
    private val engine = SnakeEngine(BOARD_COLS, BOARD_ROWS)
    private var loop: Job? = null

    private val _state = MutableStateFlow(snapshot(Phase.MENU, prefs.controlMode, prefs.highScore))
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<GameEvent>(extraBufferCapacity = 8)
    val events: SharedFlow<GameEvent> = _events.asSharedFlow()

    fun selectMode(mode: ControlMode) {
        prefs.controlMode = mode
        _state.update { it.copy(mode = mode) }
        _events.tryEmit(GameEvent.CLICK)
    }

    fun startGame() {
        engine.reset()
        _state.value = snapshot(Phase.PLAYING, _state.value.mode, _state.value.highScore)
        _events.tryEmit(GameEvent.START)
        startLoop()
    }

    fun pause() {
        if (_state.value.phase != Phase.PLAYING) return
        loop?.cancel()
        _state.update { it.copy(phase = Phase.PAUSED) }
    }

    fun resume() {
        if (_state.value.phase != Phase.PAUSED) return
        _state.update { it.copy(phase = Phase.PLAYING) }
        _events.tryEmit(GameEvent.CLICK)
        startLoop()
    }

    fun toMenu() {
        loop?.cancel()
        engine.reset()
        _state.value = snapshot(Phase.MENU, _state.value.mode, _state.value.highScore)
        _events.tryEmit(GameEvent.CLICK)
    }

    fun turnLeft() {
        if (_state.value.phase == Phase.PLAYING) engine.turnLeft()
    }

    fun turnRight() {
        if (_state.value.phase == Phase.PLAYING) engine.turnRight()
    }

    fun steer(direction: Direction) {
        if (_state.value.phase == Phase.PLAYING) engine.steer(direction)
    }

    private fun startLoop() {
        loop?.cancel()
        loop = viewModelScope.launch {
            while (isActive) {
                delay(TICK_MS)
                if (!tick()) break
            }
        }
    }

    /** Runs one engine step. Returns false when the game is over. */
    private fun tick(): Boolean {
        val result = engine.step()
        val current = _state.value
        return when (result) {
            StepResult.MOVED -> {
                _state.value = snapshot(Phase.PLAYING, current.mode, current.highScore)
                true
            }
            StepResult.ATE -> {
                _state.value = snapshot(Phase.PLAYING, current.mode, current.highScore)
                _events.tryEmit(GameEvent.EAT)
                true
            }
            StepResult.DIED -> {
                val best = maxOf(current.highScore, engine.score)
                if (best > current.highScore) prefs.highScore = best
                _state.value = snapshot(Phase.GAME_OVER, current.mode, best)
                _events.tryEmit(GameEvent.GAME_OVER)
                false
            }
        }
    }

    private fun snapshot(phase: Phase, mode: ControlMode, highScore: Int) = GameUiState(
        phase = phase,
        mode = mode,
        snake = engine.snake,
        food = engine.food,
        direction = engine.direction,
        score = engine.score,
        highScore = highScore,
    )
}
