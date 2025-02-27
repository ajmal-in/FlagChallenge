package com.apdre.flagchallenge.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.apdre.flagchallenge.Screen
import com.apdre.flagchallenge.ui.screens.scheduler.ChallengeSchedulerPage
import com.apdre.flagchallenge.ui.screens.game.GameScreenView
import com.apdre.flagchallenge.ui.screens.game.GameScreenViewModel
import com.apdre.flagchallenge.ui.screens.scheduler.ChallengeSchedulerScreenViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    destination: String = Screen.ScheduleChallengeScreen.route
) {
    NavHost(navController, startDestination = destination) {
        composable(route = Screen.ScheduleChallengeScreen.route) {
            val viewModel: ChallengeSchedulerScreenViewModel = hiltViewModel()
            ChallengeSchedulerPage(modifier = modifier, viewModel = viewModel) {
                navController.navigate(Screen.GameScreen.route)
            }
        }

        composable(route = Screen.GameScreen.route) {
            val gameScreenViewModel: GameScreenViewModel = hiltViewModel()
            GameScreenView(modifier = modifier, viewModel = gameScreenViewModel) {
                navController.navigate(Screen.ScheduleChallengeScreen.route)
            }
        }
    }
}