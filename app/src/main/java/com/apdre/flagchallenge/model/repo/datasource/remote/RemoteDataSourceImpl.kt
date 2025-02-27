package com.apdre.flagchallenge.model.repo.datasource.remote

import com.apdre.flagchallenge.model.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(private val apiService: ApiService) : RemoteDataSource {
    override suspend fun getQuestions(): List<Question> {
        return  apiService.getQuestions().questions
    }
}