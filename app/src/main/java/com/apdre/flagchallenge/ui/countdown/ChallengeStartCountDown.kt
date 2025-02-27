package com.apdre.flagchallenge.ui.countdown

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apdre.flagchallenge.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Preview
@Composable
fun ChallengeCountDownScreen(
    modifier: Modifier = Modifier,
    countDownSeconds: Long = 10,
    onCountdownFinished: () -> Unit = {}
) {
    CountDownMessage(modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp), countDownSeconds)
}

@Preview
@Composable
fun CountDownMessage(modifier: Modifier = Modifier, countDownSeconds: Long = 10) {

    Column(modifier = modifier.padding(10.dp, 18.dp).fillMaxWidth()) {
        Text(
            text = stringResource(R.string.challenge_start_in),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Text(
            text = formatSecondsToTime(countDownSeconds),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }

}




@SuppressLint("DefaultLocale")
fun formatSecondsToTime(seconds: Long): String {
    println("on change seconds = $seconds")
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    if (hours > 0 ) {
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    } else {
        return String.format("%02d:%02d", minutes, secs)
    }

}

fun formatDate(date: Date?, pattern: String = "dd-MM-yyyy HH:mm:ss"): String {
    if (date == null) {
        return ""
    }
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(date)
}
