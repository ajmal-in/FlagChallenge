package com.apdre.flagchallenge.ui.screens.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.apdre.flagchallenge.R
import com.apdre.flagchallenge.model.Option
import com.apdre.flagchallenge.ui.theme.AppBarColor
import com.apdre.flagchallenge.ui.theme.ButtonColor
import com.apdre.flagchallenge.ui.theme.CorrectAnswerColor
import com.apdre.flagchallenge.ui.theme.GameOverTextColor
import com.apdre.flagchallenge.ui.theme.WrongAnswerColor
import com.apdre.flagchallenge.ui.utils.BaseCardView
import com.apdre.flagchallenge.ui.utils.GameButton
import kotlinx.coroutines.delay

@Preview
@Composable
fun GameScreenView(
    modifier: Modifier = Modifier,
    viewModel: GameScreenViewModel = hiltViewModel(),
    onGameResets: () -> Unit = {}
) {

    val timer by viewModel.timer.collectAsStateWithLifecycle()

    BaseCardView(modifier = modifier, timeOut = timer) {

        val gameState by viewModel.gameState.collectAsStateWithLifecycle()

        //Show UI based on game state
        when (gameState) {

            GameState.Loading -> {
                LoadingPage()
            }

            GameState.Restart -> {
                onGameResets()
            }

            GameState.Started -> {
                val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
                val questions by viewModel.questions.collectAsStateWithLifecycle()
                val selectedAnswer by viewModel.selectedAnswer.collectAsStateWithLifecycle()
                val showAnswer by viewModel.isQuestionTimeout.collectAsStateWithLifecycle()

                if (currentQuestionIndex > -1 && questions.isNotEmpty() && questions.size > currentQuestionIndex) {
                    val question = questions[currentQuestionIndex]
                    QuestionCard(
                        code = question.code.uppercase(),
                        currentQuestionIndex,
                        options = question.options,
                        answerId = question.answerId,
                        selectedAnswer = selectedAnswer,
                        showAnswer = showAnswer
                    ) {
                        viewModel.selectAnswer(it.id)
                    }
                }
            }


            GameState.Ended -> {

                val score by viewModel.score.collectAsStateWithLifecycle()

                var showGameOver by remember { mutableStateOf(true) }
                var showScore by remember { mutableStateOf(false) }

                LaunchedEffect(true) {
                    delay(1600L)
                    showGameOver = false
                    delay(500L)
                    showScore = true
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(340.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(
                        visible = showGameOver,
                        enter = fadeIn(animationSpec = tween(500)),
                        exit = fadeOut(animationSpec = tween(500))
                    ) {
                        Text(
                            text = "Game Over",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = GameOverTextColor
                        )
                    }

                    AnimatedVisibility(
                        visible = showScore,
                        enter = fadeIn(animationSpec = tween(500)) + scaleIn(),
                        exit = fadeOut(animationSpec = tween(800))
                    ) {

                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = "Score: $score / 100",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            GameButton(
                                stringResource(R.string.restart),
                                modifier = Modifier.align(Alignment.BottomCenter)
                            ) {
                                viewModel.resetGame()
                            }
                        }


                    }
                }
            }
        }


    }

}


@Composable
fun LoadingPage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(340.dp),
    ) {

        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = ButtonColor)
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(R.string.loading),
                fontWeight = FontWeight.Bold
            )
        }

    }
}


@Preview
@Composable
fun QuestionCard(
    code: String = "",
    currentQuestionIndex: Int = 0,
    options: List<Option> = emptyList(),
    answerId: Int = -1,
    selectedAnswer: Int? = null,
    showAnswer: Boolean = false,
    onOptionSelected: (Option) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 28.dp),
                text = stringResource(R.string.quess_country_question).uppercase(),
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssetImage("${code}.png")
                Spacer(modifier = Modifier.width(10.dp))
                ChoiceGrid(
                    options = options,
                    selectedAnswer = selectedAnswer,
                    showAnswer = showAnswer,
                    answer = answerId,
                    onOptionSelected = onOptionSelected
                )
            }
        }
        QuestionNumber(currentQuestionIndex + 1)
    }
}

@Composable
fun AssetImage(imageFileName: String) {
    Image(
        painter = rememberAsyncImagePainter("file:///android_asset/data/flags/$imageFileName"),
        contentDescription = "Asset Image",
        modifier = Modifier.size(width = 71.dp, height = 53.dp)
    )
}

@Preview
@Composable
fun QuestionNumber(
    number: Int = 1, circleColor: Color = AppBarColor, textColor: Color = Color.White
) {

    Box(contentAlignment = Alignment.Center, modifier = Modifier.background(Color.Black)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(8.dp)
                .size(26.dp)
                .clip(CircleShape)
                .background(circleColor)
        ) {
            Text(
                text = number.toString(),
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

    }
}


@Composable
fun ChoiceGrid(
    options: List<Option>,
    answer: Int = -1,
    selectedAnswer: Int?,
    showAnswer: Boolean = false,
    onOptionSelected: (Option) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(options) {
                AnswerButton(
                    option = it,
                    answerId = answer,
                    selectedAnswerId = selectedAnswer,
                    showAnswer = showAnswer
                ) {
                    onOptionSelected(it)
                }
            }
        }
    }
}

@Composable
fun AnswerButton(
    modifier: Modifier = Modifier,
    option: Option,
    answerId: Int = -1,
    selectedAnswerId: Int? = -1,
    showAnswer: Boolean = false,
    onClick: () -> Unit
) {

    Column(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .defaultMinSize(minHeight = 42.dp)
    ) {

        var buttonBorderColor by remember { mutableStateOf(Color.Black) }

        buttonBorderColor = if (showAnswer) {
            if (answerId == option.id) {
                CorrectAnswerColor
            } else if (selectedAnswerId != null && selectedAnswerId == option.id) { //means user selected wrong answer
                WrongAnswerColor
            } else {
                Color.Black
            }
        } else {
            Color.Black
        }

        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(
                1.dp, buttonBorderColor
            ),
            colors = ButtonDefaults.buttonColors(containerColor = if (selectedAnswerId == option.id) ButtonColor else Color.Transparent)
        ) {
            Text(
                option.name,
                fontSize = 13.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp,
                maxLines = 2
            )
        }

        var message = ""

        if (showAnswer) {
            if (answerId == option.id) {
                //show correct message
                message = stringResource(R.string.correct).uppercase()
            } else if (selectedAnswerId != null && selectedAnswerId > 0 && selectedAnswerId == option.id) {
                message = stringResource(R.string.wrong).uppercase()
            }
        }

        Text(
            text = message,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 12.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Bold,
            color = buttonBorderColor
        )
    }

}


