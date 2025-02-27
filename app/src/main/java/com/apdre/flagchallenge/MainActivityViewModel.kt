package com.apdre.flagchallenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apdre.flagchallenge.database.ChallengeScheduleEntity
import com.apdre.flagchallenge.model.repo.challenge.ChallengeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val challengeRepository: ChallengeRepository) :
    ViewModel() {

    private val _scheduledChallenge = MutableStateFlow<ChallengeScheduleEntity?>(null)
    val scheduledChallenge = _scheduledChallenge.asStateFlow()

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()


    init {
        loadLastScheduledChallenge()
    }


    private fun loadLastScheduledChallenge() {
        viewModelScope.launch {
            val challenge = challengeRepository.getLastScheduledChallenge()
            _scheduledChallenge.value = challenge
            _state.value = State.SUCCESS
        }
    }

    sealed class State {
        data object Loading : State()
        data object SUCCESS : State()
    }

}