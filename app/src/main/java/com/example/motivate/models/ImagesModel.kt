package com.example.motivate.models

import com.google.gson.annotations.SerializedName

data class ImagesModel(
        @SerializedName("id") var id: String,
        @SerializedName("author") var author: String,
        @SerializedName("width") var width: String,
        @SerializedName("height") var height: String,
        @SerializedName("url") var url: String,
        @SerializedName("download_url") var download_url: String
)