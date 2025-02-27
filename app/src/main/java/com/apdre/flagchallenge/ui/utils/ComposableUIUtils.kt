package com.apdre.flagchallenge.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apdre.flagchallenge.R
import com.apdre.flagchallenge.ui.countdown.formatSecondsToTime
import com.apdre.flagchallenge.ui.theme.ButtonColor


@Preview
@Composable
fun BaseCardView(
    modifier: Modifier = Modifier,
    timeOut: Int = -1,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            CardHeader(timeOut)
            HorizontalDivider()
            content()
            //ChallengeScheduler()
        }

    }
}

@Preview
@Composable
fun CardHeader(timeOut: Int = -1) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(50.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.app_name).uppercase(), fontWeight = FontWeight.Bold)
        TimeOutView(modifier = Modifier.align(Alignment.CenterStart), timeOut)
    }
}

@Composable
fun TimeOutView(modifier: Modifier = Modifier, timeOut: Int = -1) {
    if (timeOut > -1) {
        Text(
            modifier = modifier
                .background(Color.Black, RoundedCornerShape(4.dp, 4.dp, 4.dp, 4.dp))
                .padding(10.dp, 10.dp),
            text = formatSecondsToTime(timeOut.toLong()),
            color = Color.White
        )
    }

}


@Composable
fun SingleDigitPicker(
    selectedValue: Int,
    onValueChange: (Int) -> Unit
) {
    val range = 0..9
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedValue)

    LaunchedEffect(selectedValue) {
        listState.animateScrollToItem(selectedValue)
    }

    Box(
        modifier = Modifier
            .height(100.dp)
            .width(80.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 32.dp)
        ) {

            items(range.toList()) {
                it
                Text(
                    text = it.toString(),
                    fontSize = 28.sp,
                    fontWeight = if (it == selectedValue) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            onValueChange(it)
                        },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun SingleDigitTimePicker(
    selectedValue: Int,
    bgColor: Color = Color(0xFFD9D9D9),
    onValueChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = (0..9).toList()

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.Center)
            .clickable { expanded = true }
            //            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
//            .padding(8.dp)
            .padding(4.dp)
            .background(bgColor)
            .padding(10.dp)
            .size(28.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxSize(),
            text = selectedValue.toString(),
            fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
            color = Color.Black
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { number ->
                DropdownMenuItem(
                    text = { Text(text = number.toString(), fontSize = 18.sp) },
                    onClick = {
                        onValueChange(number)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun GameButton(
    text: String,
    modifier: Modifier = Modifier,
    bgColor: Color = ButtonColor,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
        onClick = {
            onClick()
        }) {
        Text(
            text = text,
            fontSize = dimensionResource(R.dimen.save_button).value.sp,
            modifier = Modifier.padding(12.dp, 2.dp),
            color = Color.White
        )
    }
}