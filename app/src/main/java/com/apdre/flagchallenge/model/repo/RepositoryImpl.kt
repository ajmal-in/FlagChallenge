package com.apdre.flagchallenge.model.repo

import android.content.Context
import com.apdre.flagchallenge.model.Question
import com.apdre.flagchallenge.model.repo.datasource.local.LocalDataSource
import com.apdre.flagchallenge.model.repo.datasource.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val context: Context,
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : Repository {
    override suspend fun fetchQuestionsFromRemoteSource(): List<Question> {
        return remoteDataSource.getQuestions()
    }

    override suspend fun fetchQuestionsFromLocalSource(): List<Question> {
        return localDataSource.getQuestions()
    }
}