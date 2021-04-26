package com.example.motivate.models

import com.google.gson.annotations.SerializedName

data class QuotesModel(
        @SerializedName("text")  var quote: String,
        @SerializedName("author") var author: String
)