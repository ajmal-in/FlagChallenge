package com.apdre.flagchallenge.model.repo

import com.apdre.flagchallenge.model.Question
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun fetchQuestionsFromRemoteSource(): List<Question>
    suspend fun fetchQuestionsFromLocalSource(): List<Question>
}