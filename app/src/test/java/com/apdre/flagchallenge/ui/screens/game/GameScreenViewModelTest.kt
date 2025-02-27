package com.apdre.flagchallenge.ui.screens.game

import com.apdre.flagchallenge.database.ChallengeScheduleEntity
import com.apdre.flagchallenge.model.Option
import com.apdre.flagchallenge.model.Question
import com.apdre.flagchallenge.model.repo.Repository
import com.apdre.flagchallenge.model.repo.challenge.ChallengeRepository
import com.apdre.flagchallenge.ui.utils.ChallengeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class GameScreenViewModelTest {

    private lateinit var viewModel: GameScreenViewModel
    private val repository: Repository = mock()
    private val challengeRepository: ChallengeRepository = mock()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loading questions should update state to Ended when challenge state is ENDED`() = runTest {
        // Given
        val mockChallenge = ChallengeScheduleEntity(
            id = 1,
            timeoutDuration = 30L,
            scheduledTime = System.currentTimeMillis(),
            lastActionTime = System.currentTimeMillis(),
            questionIndex = 0,
            score = 20,
            answerId = 1,
            state = ChallengeState.ENDED
        )
        
        whenever(challengeRepository.getLastScheduledChallenge()).thenReturn(mockChallenge)
        
        // When
        viewModel = GameScreenViewModel(repository, challengeRepository)
        
        // Then
        assertEquals(GameState.Ended, viewModel.gameState.value)
    }

    @Test
    fun `score should update when loading questions and when challenge state is ENDED`() = runTest {
        // Given
        val mockChallenge = ChallengeScheduleEntity(
            id = 1,
            timeoutDuration = 30L,
            scheduledTime = System.currentTimeMillis(),
            lastActionTime = System.currentTimeMillis(),
            questionIndex = 0,
            score = 20,
            answerId = 1,
            state = ChallengeState.ENDED
        )

        whenever(challengeRepository.getLastScheduledChallenge()).thenReturn(mockChallenge)

        // When
        viewModel = GameScreenViewModel(repository, challengeRepository)


        // Then
        assertEquals(20, viewModel.score.value)
    }

    
    @Test
    fun `selectAnswer should update score when answer is correct`() = runTest {
        // Given
        val mockChallenge = ChallengeScheduleEntity(
            id = 1,
            timeoutDuration = 0L,
            scheduledTime = System.currentTimeMillis(),
            lastActionTime = System.currentTimeMillis(),
            questionIndex = 0,
            score = 0,
            answerId = -1,
            state = ChallengeState.SCHEDULED
        )
        
        val mockQuestions = listOf(
            Question(
                options = listOf(
                    Option(1, "France"),
                    Option(2, "Canada"),
                    Option(3, "Japan"),
                    Option(4, "China")
                ),
                answerId = 1,
                code = "FR"
            )
        )
        
        whenever(challengeRepository.getLastScheduledChallenge()).thenReturn(mockChallenge)
        whenever(repository.fetchQuestionsFromLocalSource()).thenReturn(mockQuestions)
        
        // When
        viewModel = GameScreenViewModel(repository, challengeRepository)
        delay(5000)
        viewModel.selectAnswer(1) // Select correct answer
        
        // Then
        assertEquals(1, viewModel.selectedAnswer.value)
        assertEquals(100, viewModel.score.value) // 100 points for 1 question from question Array size 1
    }
    

    @Test
    fun `resetGame should clear challenge and set state to Restart`() = runTest {
        // Given
        val mockChallenge = ChallengeScheduleEntity(
            id = 1,
            timeoutDuration = 0L,
            scheduledTime = System.currentTimeMillis(),
            lastActionTime = System.currentTimeMillis(),
            questionIndex = 0,
            score = 50,
            answerId = -1,
            state = ChallengeState.ENDED
        )
        
        whenever(challengeRepository.getLastScheduledChallenge()).thenReturn(mockChallenge)
        
        // When
        viewModel = GameScreenViewModel(repository, challengeRepository)
        viewModel.resetGame()
        
        // Then
        assertEquals(GameState.Restart, viewModel.gameState.value)
    }
}