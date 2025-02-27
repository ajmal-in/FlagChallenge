package com.apdre.flagchallenge.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ChallengeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: ChallengeScheduleEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateChallenge(challenge: ChallengeScheduleEntity)

    @Query("SELECT * FROM challenge_schedule ORDER BY scheduledTime DESC LIMIT 1")
    suspend fun getLastScheduledChallenge(): ChallengeScheduleEntity?

    @Query("DELETE FROM challenge_schedule")
    suspend fun clearChallenges()
}