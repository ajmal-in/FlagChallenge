package com.apdre.flagchallenge.model

import com.google.gson.annotations.SerializedName

data class Option(val id : Int, @SerializedName("country_name") val name : String)
