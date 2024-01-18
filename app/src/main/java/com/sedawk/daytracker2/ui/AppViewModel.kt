package com.sedawk.daytracker2.ui

import androidx.lifecycle.ViewModel
import com.sedawk.daytracker2.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.util.Calendar
import java.util.Date

class AppViewModel(private val sessionManager: SessionManager? = null) : ViewModel() {
    data class ChallengeUiState(
        var challengeName: String? = null,
        var timestamp: Long? = null,
        var isStartClicked: Boolean = false,
        var isSettingClicked: Boolean = false
    )

    private val _uiState = MutableStateFlow(ChallengeUiState())
    val uiState: StateFlow<ChallengeUiState> = _uiState.asStateFlow()

    init {
        println("Viewmodel init called");
        _uiState.value.challengeName = sessionManager?.name
        _uiState.value.timestamp = sessionManager?.timestamp

        println("Challenge Name: ${_uiState.value.challengeName}")
    }

    fun setUiStateForPreview(update: ChallengeUiState.() -> ChallengeUiState) {
        _uiState.update(update)
    }

    fun changeStartClicked(isStartClicked: Boolean) {
        _uiState.update { it.copy(isStartClicked = isStartClicked) }
    }

    fun changeSettingClicked(isSettingClicked: Boolean) {
        _uiState.update { it.copy(isSettingClicked = isSettingClicked) }
    }


    fun updateTimestamp(timestamp: Long) {
        sessionManager?.timestamp = timestamp
        _uiState.update { it.copy(timestamp = timestamp) }
    }

    fun startChallenge(challengeName: String) {
        sessionManager?.name = challengeName
        val timestamp = System.currentTimeMillis()
        sessionManager?.timestamp = timestamp
        _uiState.update {
            it.copy(
                isStartClicked = false,
                challengeName = challengeName,
                timestamp = timestamp
            )
        }
    }

    fun getFormattedDates(timestamp: Long): List<Pair<String, String>> {
        val diff = System.currentTimeMillis() - timestamp
        val diffDays = diff / (24 * 60 * 60 * 1000)

        val calendar = Calendar.getInstance()
        val currentHour = calendar[Calendar.HOUR_OF_DAY]
        val currentMinute = calendar[Calendar.MINUTE]
        val currentSecond = calendar[Calendar.SECOND]
        var remainingSeconds =
            (23 - currentHour) * 60 * 60 + (59 - currentMinute) * 60 + (59 - currentSecond)
        val remainingHours = remainingSeconds / (60 * 60) // Calculate remaining hours
        remainingSeconds %= 60 * 60
        val remainingMinutes = remainingSeconds / 60 // Calculate remaining minutes
        remainingSeconds %= 60

        val timer = mutableListOf<Pair<String, String>>()
        timer.add(Pair((diffDays + 1).toString(), "Day"))
        timer.add(Pair(remainingHours.toString(), "hours"))
        timer.add(Pair(remainingMinutes.toString(), "minutes"))
        timer.add(Pair(remainingSeconds.toString(), "seconds"))

        return timer;
    }

    fun resetChallenge() {
        _uiState.update { ChallengeUiState() }
    }
}