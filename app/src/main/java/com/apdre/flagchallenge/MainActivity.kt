package com.apdre.flagchallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.apdre.flagchallenge.navigation.AppNavHost
import com.apdre.flagchallenge.ui.screens.game.LoadingPage
import com.apdre.flagchallenge.ui.theme.AppBarColor
import com.apdre.flagchallenge.ui.theme.FlagChallengeTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setStatusBarColor(AppBarColor, darkIcons = false)
            MainView()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainView() {

    FlagChallengeTheme {

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.app_name),
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    colors = TopAppBarColors(
                        AppBarColor, AppBarColor, Color.White, Color.White, Color.White
                    )
                )
            }
        ) { innerPadding ->


            val navHostController = rememberNavController()
            val viewModel: MainActivityViewModel = hiltViewModel()

            val state by viewModel.state.collectAsStateWithLifecycle()

            when(state) {
                MainActivityViewModel.State.Loading -> {
                    LoadingPage()
                }
                MainActivityViewModel.State.SUCCESS -> {
                    val scheduledChallenge = viewModel.scheduledChallenge.collectAsStateWithLifecycle()
                    if (scheduledChallenge.value != null) {
                        AppNavHost(
                            navHostController,
                            modifier = Modifier.padding(innerPadding),
                            destination = Screen.GameScreen.route
                        )
                    } else {
                        AppNavHost(
                            navHostController,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }

        }
    }
}




