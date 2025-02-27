package com.apdre.flagchallenge.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apdre.flagchallenge.database.ChallengeScheduleEntity
import com.apdre.flagchallenge.model.Question
import com.apdre.flagchallenge.model.repo.Repository
import com.apdre.flagchallenge.model.repo.challenge.ChallengeRepository
import com.apdre.flagchallenge.ui.utils.ChallengeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

@HiltViewModel
class GameScreenViewModel @Inject constructor(
    private val repository: Repository,
    private val challengeRepository: ChallengeRepository
) : ViewModel() {

    private val ANSWER_TIMEOUT = 10
    private val QUESTION_TIMEOUT = 30

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions = _questions.asStateFlow()


    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex


    private val _timer = MutableStateFlow(-1) // 20-second timer
    val timer = _timer.asStateFlow()

    private val _selectedAnswer = MutableStateFlow<Int?>(null)
    val selectedAnswer: StateFlow<Int?> = _selectedAnswer

    private var timerJob: Job? = null

    private val _isQuestionTimeout = MutableStateFlow(false)
    val isQuestionTimeout = _isQuestionTimeout.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score = _score.asStateFlow()

    private val _challenge = MutableStateFlow<ChallengeScheduleEntity?>(null)
    val challenge = _challenge.asStateFlow()


    private val _gameState= MutableStateFlow<GameState>(GameState.Loading)
    val gameState = _gameState.asStateFlow()


    init {
        loadQuestions()
    }


    private fun loadQuestions() {

        _score.value = 0 // Reset score
//        _isGameOver.value = false

        viewModelScope.launch {
            val challenge = challengeRepository.getLastScheduledChallenge()

            var skipPage = 0
            var timeoutPercetage = 0.0
            if (challenge != null) {

                if (challenge.state == ChallengeState.ENDED) {
                    _score.value = challenge.score
                    _gameState.value = GameState.Ended
                    return@launch
                }

                if (challenge.questionIndex > 0 || ((System.currentTimeMillis() - challenge.lastActionTime) > 1000)) {
                    val start = calculateStartIndex(challenge)
                    skipPage = start.first.toInt()
                    timeoutPercetage = max(min(start.second, 1.0), 0.0)
                    println("question index = ${challenge.questionIndex},  sk = ${skipPage}")
                    _currentQuestionIndex.value = challenge.questionIndex + skipPage
                } else {
                    _currentQuestionIndex.value = challenge.questionIndex
                }
                _score.value = challenge.score

                if(_currentQuestionIndex.value == challenge.questionIndex) {
                    if(challenge.state == ChallengeState.ANSWERED) {
                        _selectedAnswer.value = challenge.answerId
                    }
                }
            }

            _challenge.value = challenge


            withContext(Dispatchers.IO) {
                val questions = repository.fetchQuestionsFromLocalSource()
                _questions.value = questions
                println("questions = $questions")
                if (_currentQuestionIndex.value >= questions.size) {
                    _gameState.value = GameState.Ended
                    return@withContext
                }



                val totalTimeout = QUESTION_TIMEOUT + ANSWER_TIMEOUT
                var elapsedTime = totalTimeout * timeoutPercetage
                if (elapsedTime >= QUESTION_TIMEOUT) {
                    startAnswerVisiblityTimeout((totalTimeout - elapsedTime).toInt())
                } else {
                    startQuestionTimer((totalTimeout - elapsedTime).toInt())
                }
                _gameState.value = GameState.Started

            }

        }
    }


    private fun calculateStartIndex(challenge: ChallengeScheduleEntity): Pair<Long, Double> {

        val lastTime = if (challenge.state == ChallengeState.SCHEDULED) {
            challenge.scheduledTime + (challenge.timeoutDuration * 1000)
        } else {
            challenge.lastActionTime
        }
        val currentTime = System.currentTimeMillis()
        val questionIndex = challenge.questionIndex
        val elapsedTime = currentTime - lastTime

        var skippableQuestionCount = 0L
        var timeoutRate = 0L

        var timeOutSeconds = ANSWER_TIMEOUT + QUESTION_TIMEOUT

//        if (challenge.state == ChallengeState.ANSWERED) {
//            if (elapsedTime >= (ANSWER_TIMEOUT * 1000)) {
//                skippableQuestionCount =
//                    ((elapsedTime - (ANSWER_TIMEOUT * 1000)) / (timeOutSeconds * 1000) + 1)
//                timeoutRate = (elapsedTime - (ANSWER_TIMEOUT * 1000)) % (timeOutSeconds * 1000)
//            } else {
//                skippableQuestionCount = 0
//                timeoutRate = elapsedTime % (ANSWER_TIMEOUT * 1000)
//            }
//        } else
//        {
//            skippableQuestionCount = elapsedTime / (timeOutSeconds * 1000)
//            timeoutRate = elapsedTime % (timeOutSeconds * 1000)
//        }

        skippableQuestionCount = elapsedTime / (timeOutSeconds * 1000)
        timeoutRate = elapsedTime % (timeOutSeconds * 1000)


        timeoutRate /= 1000 // converting from milli seconds to seconds
        return Pair(skippableQuestionCount, timeoutRate / 100.0)
    }

    /**
     * This will start the countDown for showing question.
     */
    private fun startQuestionTimer(timeOut: Int = QUESTION_TIMEOUT) {
        println("Question timer == $timeOut")

        timerJob?.cancel() // Cancel previous timer if any
        _timer.value = -1

        timerJob = viewModelScope.launch {
            val challenge = _challenge.value
            if (challenge != null) {
                updateChallenge(
                    challenge.copy(
                        score = _score.value,
                        questionIndex = _currentQuestionIndex.value,
                        lastActionTime = System.currentTimeMillis(),
                        state = ChallengeState.STARTED,
                        answerId = -1
                    )
                )
            }


            for (time in timeOut downTo 0) {
                _timer.value = time
                delay(1000L) // Wait for 1 second
            }
            _isQuestionTimeout.value = true
            startAnswerVisiblityTimeout()
        }
    }

    private suspend fun updateChallenge(
        challengeScheduleEntity: ChallengeScheduleEntity?
    ) {
        if (challengeScheduleEntity != null) {
            challengeRepository.updateChallenge(challengeScheduleEntity)
        }
    }

    fun selectAnswer(answerId: Int) {
        if (_selectedAnswer.value == null && !isQuestionTimeout.value) {
            _selectedAnswer.value = answerId

            val correctAnswerId = _questions.value[_currentQuestionIndex.value].answerId
            if (answerId == correctAnswerId) {
                val pointsPerQuestion = 100 / _questions.value.size
                _score.value += pointsPerQuestion
            }

            val challenge = _challenge.value
            if (challenge != null) {
                viewModelScope.launch {
                    updateChallenge(
                        challenge.copy(
                            score = _score.value,
                            questionIndex = _currentQuestionIndex.value,
                            state = ChallengeState.ANSWERED,
                            answerId = answerId
                        )
                    )

                }
            }

            /**
             * if we need to show Answer and start answer timeout immediately after user selects an answer.
               _isQuestionTimeout.value = true
                startAnswerVisiblityTimeout()
             */
        }
    }

    /**
     * This will start the countDown for showing Answer for few seconds.
     */
    private fun startAnswerVisiblityTimeout(timeOut: Int = ANSWER_TIMEOUT) {
        timerJob?.cancel() // Stop the 20-second timer
        _timer.value = -1

        // Start an timeOut-second delay before moving to the next question
        viewModelScope.launch {
            for (time in timeOut downTo 0) {
                _timer.value = time
                delay(1000L)
            }
            moveToNextQuestion()
        }
    }

    /**
     * Function to update question index and move on to next question
     */
    private fun moveToNextQuestion() {
        val nextIndex = _currentQuestionIndex.value + 1
        if (nextIndex < _questions.value.size) {
            _currentQuestionIndex.value = nextIndex
            _isQuestionTimeout.value = false
            _selectedAnswer.value = null
            startQuestionTimer()
        } else {

            _timer.value = -1

            var challenge = _challenge.value
            if (challenge != null) {
                challenge = challenge.copy(
                    lastActionTime = System.currentTimeMillis(),
                    state = ChallengeState.ENDED
                )
                viewModelScope.launch {
                    updateChallenge(challenge)
                    _gameState.value = GameState.Ended
                }
            }

        }
    }


    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel() // Cancel the timer when ViewModel is cleared
    }

    /**
     * reset the game
     */
    fun resetGame() {
        viewModelScope.launch {
            challengeRepository.clearChallenge()
            _gameState.value = GameState.Restart
        }
    }
}


sealed class GameState {
    data object Loading : GameState()
    data object Started : GameState()
    data object Ended : GameState()
    data object Restart : GameState()
}