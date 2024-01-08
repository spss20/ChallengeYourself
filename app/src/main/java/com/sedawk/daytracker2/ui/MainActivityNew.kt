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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
                Homepage(Modifier.fillMaxSize(), appViewModel)
            }
        }

//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Homepage(modifier: Modifier = Modifier, appViewModel: AppViewModel) {
        val state = appViewModel.uiState.collectAsState().value;
        var challengeName by remember {
            mutableStateOf("")
        }
        Box(modifier = modifier) {
            Image(
                painter = painterResource(id = R.drawable.background_1),
                contentDescription = "Back Image",
                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
            )
            Surface(modifier = modifier.fillMaxSize(), color = Color(0x33000000)) {
            }
            Text(
                text = state.challengeName ?: "Krishna",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(top = 100.dp, start = 30.dp)
                    .widthIn(max = 200.dp)
            )

            DateView(modifier = Modifier.align(Alignment.Center), appViewModel)

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
                        .clickable { println("Image Clicked") })
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
                    Column {
                        Text(
                            text = "Enter Challenge Name",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
                        TextField(value = challengeName, onValueChange = { challengeName = it })
                    }
                }
            }
        }
    }

    @Composable
    fun DateView(modifier: Modifier = Modifier, appViewModel: AppViewModel) {
        Box(
            modifier = modifier
                .size(300.dp)
                .clip(
                    CircleShape
                )
                .background(color = Color(0x66FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
//            TimerView()
            StartView(
                Modifier
                    .size(140.dp)
                    .clickable { appViewModel.changeStartClicked(true) })
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
    fun TimerView(modifier: Modifier = Modifier) {
        val day = "Day 0"
        val timer = mutableListOf<Pair<String, String>>()
        timer.add(Pair("00", "hours"));
        timer.add(Pair("00", "minutes"));
        timer.add(Pair("00", "seconds"));

        Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(day, style = MaterialTheme.typography.displayLarge)

            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.padding(top = 15.dp)
            ) {
                timer.forEach() {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = it.first, style = MaterialTheme.typography.bodyLarge);
                        Text(text = it.second, style = MaterialTheme.typography.bodyMedium)
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
            isStartClicked = true,
            isSettingClicked = false
        )
        val appViewModel: AppViewModel = viewModel();
        appViewModel.setUiStateForPreview { state }

        DayTrackerTheme {
            Homepage(Modifier.fillMaxSize(), appViewModel = appViewModel)
        }
    }
}