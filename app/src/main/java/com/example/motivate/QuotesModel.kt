package com.example.motivate

import com.google.gson.annotations.SerializedName

data class QuotesModel(
        @SerializedName("text")  var quote: String,
        @SerializedName("author") var author: String
)