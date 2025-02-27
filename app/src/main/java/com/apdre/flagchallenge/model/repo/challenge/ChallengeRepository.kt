package com.apdre.flagchallenge.model.repo.challenge

import com.apdre.flagchallenge.database.ChallengeDao
import com.apdre.flagchallenge.database.ChallengeScheduleEntity
import com.apdre.flagchallenge.ui.utils.ChallengeState
import javax.inject.Inject

class ChallengeRepository@Inject constructor(private val challengeDao: ChallengeDao) {

    suspend fun scheduleChallenge(timeout: Long) {
        challengeDao.clearChallenges() // Remove old challenges
        val actionTime = System.currentTimeMillis()
        val challenge = ChallengeScheduleEntity(
            timeoutDuration = timeout,
            scheduledTime = actionTime,
            lastActionTime = actionTime,
            state = ChallengeState.SCHEDULED
        )
        challengeDao.insertChallenge(challenge)
    }

    suspend fun updateChallenge(challengeScheduleEntity: ChallengeScheduleEntity) {
        challengeDao.updateChallenge(challengeScheduleEntity)
    }

    suspend fun getLastScheduledChallenge(): ChallengeScheduleEntity? {
        return challengeDao.getLastScheduledChallenge()
    }

    suspend fun clearChallenge() {
        challengeDao.clearChallenges()
    }
}