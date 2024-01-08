package com.sedawk.daytracker2.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.sedawk.daytracker2.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

        println("Challange Name: ${_uiState.value.challengeName}")
    }

    fun setUiStateForPreview(update: ChallengeUiState.() -> ChallengeUiState) {
        _uiState.update(update)
    }

    fun changeStartClicked(isStartClicked: Boolean){
        _uiState.update { it.copy(isStartClicked = isStartClicked) }
    }

}