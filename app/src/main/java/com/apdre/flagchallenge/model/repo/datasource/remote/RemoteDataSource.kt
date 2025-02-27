package com.apdre.flagchallenge.model.repo.datasource.remote

import com.apdre.flagchallenge.model.Question
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    suspend fun getQuestions(): List<Question>
}