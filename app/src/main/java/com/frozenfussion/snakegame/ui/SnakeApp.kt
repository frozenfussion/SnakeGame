package com.frozenfussion.snakegame.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.frozenfussion.snakegame.audio.Sound
import com.frozenfussion.snakegame.audio.SfxPlayer
import com.frozenfussion.snakegame.game.ControlMode
import com.frozenfussion.snakegame.game.GameEvent
import com.frozenfussion.snakegame.game.GameViewModel
import com.frozenfussion.snakegame.game.Phase
import com.frozenfussion.snakegame.haptics.Haptics
import com.frozenfussion.snakegame.input.TiltEffect

/** Picks the right screen for the current game phase and wires up sound, vibration and sensors. */
@Composable
fun SnakeApp(tiltAvailable: Boolean, viewModel: GameViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val sfx = remember { SfxPlayer() }
    val haptics = remember { Haptics(context) }

    DisposableEffect(Unit) { onDispose { sfx.release() } }

    LaunchedEffect(tiltAvailable) {
        if (!tiltAvailable) viewModel.selectMode(ControlMode.TAP)
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                GameEvent.START -> sfx.play(Sound.START)
                GameEvent.EAT -> {
                    sfx.play(Sound.EAT)
                    haptics.eat()
                }
                GameEvent.GAME_OVER -> {
                    sfx.play(Sound.GAME_OVER)
                    haptics.gameOver()
                }
                GameEvent.CLICK -> sfx.play(Sound.CLICK)
            }
        }
    }

    // Leaving the app freezes the game instead of letting the snake crash while you are away.
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) { viewModel.pause() }

    TiltEffect(enabled = state.mode == ControlMode.TILT && state.phase == Phase.PLAYING) {
        viewModel.steer(it)
    }

    BackHandler(enabled = state.phase != Phase.MENU) {
        if (state.phase == Phase.PLAYING) viewModel.pause() else viewModel.toMenu()
    }

    Box(Modifier.fillMaxSize().background(Arcade.Background)) {
        Box(Modifier.fillMaxSize().safeDrawingPadding()) {
            when (state.phase) {
                Phase.MENU -> StartScreen(
                    state = state,
                    tiltAvailable = tiltAvailable,
                    onSelectMode = viewModel::selectMode,
                    onPlay = viewModel::startGame,
                )
                Phase.PLAYING, Phase.PAUSED -> GameScreen(
                    state = state,
                    onTurnLeft = viewModel::turnLeft,
                    onTurnRight = viewModel::turnRight,
                    onPause = viewModel::pause,
                    onResume = viewModel::resume,
                    onMenu = viewModel::toMenu,
                )
                Phase.GAME_OVER -> GameOverScreen(
                    state = state,
                    onTryAgain = viewModel::startGame,
                    onMenu = viewModel::toMenu,
                )
            }
        }
        Scanlines()
    }
}
