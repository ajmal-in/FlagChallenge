package com.apdre.flagchallenge

sealed class Screen(val route : String) {
    data object ScheduleChallengeScreen : Screen("SCHEDULE_CHALLENGE_SCREEN")
    data object GameScreen: Screen("GAME_SCREEN")
}