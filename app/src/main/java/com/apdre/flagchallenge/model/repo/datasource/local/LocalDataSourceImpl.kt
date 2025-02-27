package com.apdre.flagchallenge.model.repo.datasource.local

import android.content.Context
import com.apdre.flagchallenge.model.Question
import com.apdre.flagchallenge.model.QuestionsResponse
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException


class LocalDataSourceImpl(private val context: Context) : LocalDataSource{
    override suspend fun getQuestions(): List<Question> {
        try {
            val jsonString = context.assets.open("data/questions.json").bufferedReader().use { it.readText() }
            val gson = Gson()
            val questionsResponse = gson.fromJson(jsonString, QuestionsResponse::class.java)
            return (questionsResponse.questions)
        } catch (e: Exception) {
            if(e is CancellationException) {
                throw e
            }
            e.printStackTrace()
            return emptyList()
        }
    }

}