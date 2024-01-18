package com.sedawk.daytracker2.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sedawk.daytracker2.R
import com.sedawk.daytracker2.SessionManager
import com.sedawk.daytracker2.ui.theme.DayTrackerTheme
import kotlinx.coroutines.delay

class MainActivityNew : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val sessionManager = SessionManager(this);
            val appViewModel: AppViewModel =
                viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return AppViewModel(sessionManager) as T;
                    }
                })

            DayTrackerTheme {
                Surface {
                    Homepage(Modifier.fillMaxSize(), appViewModel)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Homepage(modifier: Modifier = Modifier, appViewModel: AppViewModel) {
        val state = appViewModel.uiState.collectAsState().value;
        var showDatePicker by remember {
            mutableStateOf(false)
        }
        var tempChallengeName by remember {
            mutableStateOf("")
        }

        Box(modifier = Modifier) {
            Image(
                painter = painterResource(id = R.drawable.background_1),
                contentDescription = "Back Image",
                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
            )
            Surface(modifier = modifier.fillMaxSize(), color = Color(0x33000000)) {
            }
            Text(
                text = state.challengeName ?: "Take a Challenge?",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(top = 100.dp, start = 30.dp)
                    .widthIn(max = 200.dp),
            )

            DateView(modifier = Modifier.align(Alignment.Center), appViewModel)

            if (state.challengeName != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                ) {
                    Image(painter = painterResource(id = R.drawable.ic_setting),
                        contentDescription = "Setting",
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier
                            .padding(all = 15.dp)
                            .clickable { appViewModel.changeSettingClicked(true) })
                }
            }
        }

        if (state.isStartClicked) {
            AlertDialog(
                onDismissRequest = { appViewModel.changeStartClicked(false) },
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(16.dp)
                    )
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Enter Challenge Name",
                    )
                    TextField(
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 20.dp)
                            .background(Color.Transparent),
                        value = tempChallengeName,
                        onValueChange = { tempChallengeName = it },
                        placeholder = {
                            Text(
                                "eg. 100 days of code"
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        singleLine = true,
                    )
                    Button(
                        modifier = Modifier.align(Alignment.End),
                        onClick = {
                            appViewModel.startChallenge(tempChallengeName)
                        }) {
                        Text(
                            text = "Start Challenge",
                        )
                    }
                }
            }
        }

        if (state.isSettingClicked) {
            AlertDialog(
                onDismissRequest = { appViewModel.changeSettingClicked(false) },
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Settings")
                    Button(
                        onClick = { showDatePicker = true },
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            "Start Counter On", color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                    Button(
                        onClick = { appViewModel.resetChallenge() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Reset", color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 10.dp)
                            .clickable { appViewModel.changeSettingClicked(false) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Cancel",
                            fontSize = 16.sp
                        )
                        Icon(
                            painterResource(id = R.drawable.ic_next),
                            contentDescription = "Cancel", tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()

            val confirmEnabled by remember {
                derivedStateOf { datePickerState.selectedDateMillis != null }
            }

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false;
                        appViewModel.changeSettingClicked(false);
                        println("Selected Date ${datePickerState.selectedDateMillis}")
                        appViewModel.updateTimestamp(datePickerState.selectedDateMillis!!)
                    }, enabled = confirmEnabled) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Dismiss")
                    }
                }) {
                DatePicker(state = datePickerState)
            }
        }
    }

    @Composable
    fun Testing() {
        Box(){
            Text(modifier = Modifier.align(Alignment.Center) , text = "fddsf")
        }
    }

    @Composable
    fun DateView(modifier: Modifier = Modifier, appViewModel: AppViewModel) {
        val uiState = appViewModel.uiState.collectAsState().value
        Box(
            modifier = modifier
                .size(300.dp)
                .clip(
                    CircleShape
                )
                .background(color = Color(0x66FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.challengeName != null) {
                var timerPair: List<Pair<String, String>> by remember {
                    mutableStateOf(appViewModel.getFormattedDates(uiState.timestamp!!))
                }
                LaunchedEffect(uiState.timestamp) {
                    while (true) {
                        delay(1000)
                        timerPair = appViewModel.getFormattedDates(uiState.timestamp!!)
                    }
                }
                TimerView(timerPair)
            } else {
                StartView(
                    Modifier
                        .size(140.dp)
                        .clickable { appViewModel.changeStartClicked(true) })
            }
        }
    }

    @Composable
    fun StartView(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "START",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    @Composable
    fun TimerView(timerPair: List<Pair<String, String>>, modifier: Modifier = Modifier) {
        println("Recomposition TimerView")
        Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "${timerPair[0].second} ${timerPair[0].first}",
                style = MaterialTheme.typography.displayLarge
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.padding(top = 15.dp)
            ) {
                for (i in 1 until timerPair.size) {
                    val pair = timerPair[i];
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = pair.first, style = MaterialTheme.typography.bodyLarge);
                        Text(text = pair.second, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun Main() {
        val state = AppViewModel.ChallengeUiState(
            challengeName = "Surya Pratap Singh",
            timestamp = 1704630853535L,
            isStartClicked = false,
            isSettingClicked = true
        )
        val appViewModel: AppViewModel = viewModel();
        appViewModel.setUiStateForPreview { state }

        DayTrackerTheme {
            Homepage(Modifier.fillMaxSize(), appViewModel = appViewModel)
        }
    }
}