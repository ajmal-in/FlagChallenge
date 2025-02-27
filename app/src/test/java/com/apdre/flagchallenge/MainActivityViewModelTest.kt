package com.apdre.flagchallenge

import com.apdre.flagchallenge.database.ChallengeScheduleEntity
import com.apdre.flagchallenge.model.repo.challenge.ChallengeRepository
import com.apdre.flagchallenge.ui.utils.ChallengeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class MainActivityViewModelTest {

    private lateinit var viewModel: MainActivityViewModel
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
    fun `when repository returns challenge, then state is SUCCESS and challenge is updated`() = runTest {
        // Given
        val mockChallenge = ChallengeScheduleEntity(
            id = 1,
            timeoutDuration = 25L,
            scheduledTime = System.currentTimeMillis(),
            lastActionTime =  System.currentTimeMillis(),
            state = ChallengeState.SCHEDULED
        )

        whenever(challengeRepository.getLastScheduledChallenge()).thenReturn(mockChallenge)

        // When
        viewModel = MainActivityViewModel(challengeRepository)

        // Then
        assertEquals(MainActivityViewModel.State.SUCCESS, viewModel.state.value)
        assertEquals(mockChallenge, viewModel.scheduledChallenge.first())
    }

    @Test
    fun `when repository returns null, then state is SUCCESS and challenge is null`() = runTest {
        // Given
        whenever(challengeRepository.getLastScheduledChallenge()).thenReturn(null)

        // When
        viewModel = MainActivityViewModel(challengeRepository)

        // Then
        assertEquals(MainActivityViewModel.State.SUCCESS, viewModel.state.value)
        assertNull(viewModel.scheduledChallenge.first())
    }
}