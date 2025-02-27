package com.apdre.flagchallenge.ui.screens.scheduler

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apdre.flagchallenge.R
import com.apdre.flagchallenge.ui.countdown.ChallengeCountDownScreen
import com.apdre.flagchallenge.ui.countdown.formatDate
import com.apdre.flagchallenge.ui.countdown.formatSecondsToTime
import com.apdre.flagchallenge.ui.screens.game.LoadingPage
import com.apdre.flagchallenge.ui.utils.BaseCardView
import com.apdre.flagchallenge.ui.utils.GameButton
import com.apdre.flagchallenge.ui.utils.SingleDigitTimePicker
import kotlinx.coroutines.delay
import java.util.Date


@Preview(showBackground = true)
@Composable
fun ChallengeSchedulerPage(
    modifier: Modifier = Modifier,
    viewModel: ChallengeSchedulerScreenViewModel = hiltViewModel(),
    onStartGame: () -> Unit = {}
) {

    BaseCardView(modifier = modifier) {

        val schedulerState by viewModel.state.collectAsStateWithLifecycle()

        when (schedulerState) {

            ChallengeSchedulerScreenViewModel.SchedulerState.Loading -> {
                LoadingPage()
            }

            ChallengeSchedulerScreenViewModel.SchedulerState.NotScheduled -> {

                var scheduleTimeoutSec by remember { mutableLongStateOf(0) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    ScheduleHeading(stringResource(R.string.schedule_in).uppercase())
                    TimePicker() {
                        scheduleTimeoutSec = it
                    }
                    GameButton(stringResource(R.string.save)) {
                        viewModel.scheduleChallenge(scheduleTimeoutSec)
                    }
                }

            }

            ChallengeSchedulerScreenViewModel.SchedulerState.Scheduled -> {
                val countDownSeconds by viewModel.countDownSeconds.collectAsStateWithLifecycle()
                println("count down time --- $countDownSeconds")
                if (countDownSeconds <= 20) {
                    ChallengeCountDownScreen(countDownSeconds = countDownSeconds)
                } else {
                    val startTime by viewModel.startTime.collectAsStateWithLifecycle()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .padding(top = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        ScheduleHeading(stringResource(R.string.scheduled).uppercase())
                        if (startTime != null) {
                            Text("start time : ${formatDate(startTime)} ")
                            Text("starts In: ${formatSecondsToTime(countDownSeconds)}")
                        }

                    }
                }

            }

            ChallengeSchedulerScreenViewModel.SchedulerState.Started -> {
                onStartGame()
            }

        }


        val scheduledChallenge by viewModel.scheduledChallenge.collectAsStateWithLifecycle()

        if (scheduledChallenge == null) {

        } else {
            var startTime by remember { mutableStateOf<Date?>(null) }
            var startInSeconds by remember { mutableLongStateOf(0) }
            LaunchedEffect(scheduledChallenge) {
                val challenge = scheduledChallenge
                if (challenge != null) {
                    val scheduledTime = challenge.scheduledTime + (challenge.timeoutDuration * 1000)
                    val scheduledDateTime = Date(scheduledTime)
                    startTime = scheduledDateTime
                    var currentTimeMillis = System.currentTimeMillis()
                    while (currentTimeMillis < scheduledDateTime.time) {
                        delay(1000)
                        currentTimeMillis = System.currentTimeMillis()
                        startInSeconds =
                            (scheduledDateTime.time - currentTimeMillis) / 1000 //calculating value in seconds
                    }

                }
            }
//            val startTime = Date( scheduledChallenge!!.scheduledTime + (scheduledChallenge!!.timeoutDuration * 1000))
            println("challenge countdoen === $startInSeconds")
            if (startInSeconds in 1..20) {

            } else {

            }
        }

    }

}

@Composable
fun ScheduleHeading(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = dimensionResource(R.dimen.scheduler_heading).value.sp
    )
}


@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    bgColor: Color = Color(0xFFD9D9D9),
    onValueChanged: (seconds: Long) -> Unit
) {
    var hour by remember { mutableIntStateOf(0) }
    var minute by remember { mutableIntStateOf(0) }
    var second by remember { mutableIntStateOf(0) }

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        TwoBoxTimeComponent(stringResource(R.string.hour), value = hour, boxBG = bgColor) {
            hour = it
            val time = ((hour.toLong() * 3600) + (minute * 60) + second)
            onValueChanged(time)
        }
        TwoBoxTimeComponent(stringResource(R.string.minute), value = minute, boxBG = bgColor) {
            minute = it
            val time = ((hour.toLong() * 3600) + (minute * 60) + second)
            onValueChanged(time)
        }
        TwoBoxTimeComponent(stringResource(R.string.second), value = second, boxBG = bgColor) {
            second = it
            val time = ((hour.toLong() * 3600) + (minute * 60) + second)
            onValueChanged(time)
        }
    }

}

@Composable
fun TwoBoxTimeComponent(
    heading: String,
    value: Int = 0,
    boxBG: Color = Color(0xFFD9D9D9),
    onValueChanged: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = heading)
        Row() {
            val tenth = value / 10
            val ones = value % 10
            SingleDigitTimePicker(tenth, bgColor = boxBG) {
                val newValue = (it * 10) + ones
                onValueChanged(newValue)
            }
            SingleDigitTimePicker(ones, bgColor = boxBG) {
                val newValue = (tenth * 10) + it
                onValueChanged(newValue)
            }
        }
    }
}









