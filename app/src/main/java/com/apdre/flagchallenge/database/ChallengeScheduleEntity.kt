package com.apdre.flagchallenge.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "challenge_schedule")
data class ChallengeScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    /** Timeout from scheduled time in seconds
     * so actual time to start challenge is will be (scheduledTime + timeoutDuration)  **/
    val timeoutDuration: Long,

    /**This is the time at which user shedules the timeout to start challenge**/
    val scheduledTime: Long, // Timestamp when the challenge was scheduled

    /**
     * Users last action time.
     */
    val lastActionTime: Long = 0,

    /**
     * Can be "SCHEDULED", "STARTED", "ENDED"
     */
    val state: String = "",

    val answerId: Int = -1,

    /**
     * Can be "answered"
     */
    val questionIndex: Int = 0,


    val score: Int = 0,
)