package com.apdre.flagchallenge.model.repo.datasource.remote

import com.apdre.flagchallenge.model.QuestionsResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import javax.inject.Inject

class ApiServiceImpl@Inject constructor(val backendApiRetroService: BackendApiRetroService) : ApiService() {
    override suspend fun getQuestions(): QuestionsResponse {
        return backendApiRetroService.fetchQuestions()
    }
}


interface BackendApiRetroService {
    @Headers("Content-Type: application/json")
    @GET(ApiService.QUESTIONS)
    suspend fun fetchQuestions() : QuestionsResponse

}