package com.apdre.flagchallenge.model

import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("countries") val options: List<Option>,
    @SerializedName("answer_id") val answerId: Int,
    @SerializedName("country_code") val code: String
)