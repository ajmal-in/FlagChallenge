package com.apdre.flagchallenge.model.repo.datasource.local

import com.apdre.flagchallenge.model.Question
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun getQuestions(): List<Question>
}