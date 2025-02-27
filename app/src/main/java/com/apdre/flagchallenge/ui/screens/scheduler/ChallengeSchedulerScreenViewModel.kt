package com.apdre.flagchallenge.ui.screens.scheduler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apdre.flagchallenge.database.ChallengeScheduleEntity
import com.apdre.flagchallenge.model.repo.challenge.ChallengeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ChallengeSchedulerScreenViewModel @Inject constructor(private val challengeRepository: ChallengeRepository) :
    ViewModel() {

    private val _scheduledChallenge = MutableStateFlow<ChallengeScheduleEntity?>(null)
    val scheduledChallenge = _scheduledChallenge.asStateFlow()

    private val _countDownSeconds = MutableStateFlow(-1L)
    val countDownSeconds = _countDownSeconds.asStateFlow()

    private val _startTime = MutableStateFlow<Date?>(null)
    val startTime = _startTime.asStateFlow()


    private val _state = MutableStateFlow<SchedulerState>(SchedulerState.Loading)
    val state = _state.asStateFlow()

    init {
        loadLastScheduledChallenge()
    }

    fun scheduleChallenge(timeout: Long) {
        viewModelScope.launch {
            challengeRepository.scheduleChallenge(timeout)
            fetchLastestSchedule()
        }
    }

    private suspend fun fetchLastestSchedule() {
        val challenge = challengeRepository.getLastScheduledChallenge()
        _scheduledChallenge.value = challenge
        if(challenge != null) {
            val scheduledTime = challenge.scheduledTime + (challenge.timeoutDuration * 1000)
            val scheduledDateTime = Date(scheduledTime)
            _startTime.value = scheduledDateTime
            var currentTimeMillis = System.currentTimeMillis()

            if (currentTimeMillis >= scheduledDateTime.time) {
                _state.value = SchedulerState.Started
            } else {
                _state.value = SchedulerState.Scheduled
                var remainingSeconds =  (scheduledDateTime.time - currentTimeMillis) / 1000
                while (remainingSeconds > 0) {
                    _countDownSeconds.value = remainingSeconds
                    delay(1000)
                    remainingSeconds--
                    println("remaining secondsss = $remainingSeconds")
                }
                _state.value = SchedulerState.Started
            }

        } else {
            _state.value = SchedulerState.NotScheduled
        }
    }

    private fun loadLastScheduledChallenge() {
        viewModelScope.launch {
            fetchLastestSchedule()
        }
    }


    sealed class SchedulerState {
        data object Loading : SchedulerState()
        data object NotScheduled : SchedulerState()
        data object Scheduled : SchedulerState()
        data object Started : SchedulerState()
    }

}