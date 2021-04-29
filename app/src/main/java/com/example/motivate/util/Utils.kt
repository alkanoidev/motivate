package com.example.motivate.util

import android.content.res.Resources
import com.example.motivate.models.ImagesModel
import com.example.motivate.models.QuotesModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

class Utils {

    private lateinit var quoteList: MutableList<QuotesModel>
    private lateinit var imageList: MutableList<ImagesModel>

    private val quotesApiURL: String = "https://type.fit/api/quotes"
    private var imagesURL: String = "https://picsum.photos/v2/list"

    suspend fun getQuotes(): MutableList<QuotesModel> = coroutineScope {
        val url: URL = async(Dispatchers.IO) {
            URL(quotesApiURL)
        }.await()

        url.getString().apply {
            quoteList = async(Dispatchers.Default) { parseJsonData(this@apply) }.await()
        }
        return@coroutineScope quoteList

    }

    suspend fun getImages(): MutableList<ImagesModel> = coroutineScope {
        val url: URL = async(Dispatchers.IO) {
            URL(imagesURL)
        }.await()

        url.getString().apply {
            imageList = async(Dispatchers.Default) { parseJsonImage(this@apply) }.await()
        }
        return@coroutineScope imageList

    }

    private fun URL.getString(): String {
        val stream = openStream()
        return try {
            val r = BufferedReader(InputStreamReader(stream))
            val result = StringBuilder()
            var line: String?
            while (r.readLine().also { line = it } != null) {
                result.append(line).appendln()
            }
            result.toString()
        } catch (e: IOException) {
            e.toString()
        }
    }

    private fun parseJsonData(data: String): MutableList<QuotesModel> {
        val list: MutableList<QuotesModel> = Gson().fromJson<MutableList<QuotesModel>>(data)
        list.forEach {
            if (it.author == null) {
                it.author = "Unknown"
            }
        }
        return list
    }

    private fun parseJsonImage(data: String): MutableList<ImagesModel> {
        val list: MutableList<ImagesModel> = Gson().fromJson<MutableList<ImagesModel>>(data)
        return list
    }

    inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object : TypeToken<T>() {}.type)

    fun getScreenWidth(): String {
        return Resources.getSystem().displayMetrics.widthPixels.toString()
    }

    fun getScreenHeight(): String {
        return Resources.getSystem().displayMetrics.heightPixels.toString()
    }
}
