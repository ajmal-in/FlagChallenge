package com.apdre.flagchallenge.model.repo.datasource.remote

import com.apdre.flagchallenge.model.Question
import com.apdre.flagchallenge.model.QuestionsResponse

abstract class ApiService {
    companion object {
        const val BASEURL = "http://127.0.0.1:8080"
        const val QUESTIONS = "$BASEURL/questions"  //

    }

    abstract suspend fun getQuestions(): QuestionsResponse
}