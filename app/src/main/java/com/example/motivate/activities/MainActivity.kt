package com.example.motivate.activities

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.motivate.models.ImagesModel
import com.example.motivate.models.QuotesModel
import com.example.motivate.R
import com.example.motivate.activities.SplashScreen.Companion.context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var quoteView: TextView
    private lateinit var authorView: TextView
    private lateinit var imageView: ImageView

    private var thread = Thread()

    private val quotesApiURL:String = "https://type.fit/api/quotes"
    private var imagesURL:String = "https://picsum.photos/v2/list"
    //APPLICATION MAY BE DOING TOO MUCH WORK ON ITS MAIN THREAD

    private lateinit var quoteList:MutableList<QuotesModel>
    private lateinit var imageList:MutableList<ImagesModel>
    private lateinit var instanceQuotes: QuotesModel
    private lateinit var instanceImages: ImagesModel

    private val TAG: String = MainActivity::class.java.simpleName

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        quoteView = findViewById(R.id.quote)
        authorView = findViewById(R.id.author)
        imageView = findViewById(R.id.imageView)

        thread = Thread(Runnable {
            val url:URL? = try {
                URL(quotesApiURL)
            }catch (e: MalformedURLException){
                Log.d("Exception", e.toString())
                null
            }
            url?.getString()?.apply {
                quoteList = parseJsonData(this@apply)
            }
            val url1: URL? = try {
                URL(imagesURL)
            }catch (e: MalformedURLException){
                Log.d("Exception", e.toString())
                null
            }
            url1?.getString()?.apply {
                imageList = parseJsonImage(this@apply)
            }

            runOnUiThread {
                instanceImages = imageList[Random.nextInt(0, imageList.size - 1)]
                instanceImages.download_url = "https://picsum.photos/id/${instanceImages.id}/${getScreenWidth()}/${getScreenHeight()}/?blur=5"
                Glide.with(context).load(instanceImages.download_url).into(imageView)

            }
        })
        thread.start()

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
        }catch (e: IOException){
            e.toString()
        }
    }

    private fun parseJsonData(data:String): MutableList<QuotesModel> {
        val list:MutableList<QuotesModel> = Gson().fromJson<MutableList<QuotesModel>>(data)
        list.forEach{
            if(it.author==null){
                it.author="Unknown"
            }
        }
        return list
    }

    private fun parseJsonImage(data:String): MutableList<ImagesModel> {
        val list:MutableList<ImagesModel> = Gson().fromJson<MutableList<ImagesModel>>(data)
        return list
    }

    private inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object : TypeToken<T>() {}.type)

    fun update(view: View) {
        quoteView=findViewById(R.id.quote)
        authorView=findViewById(R.id.author)

        instanceQuotes = quoteList[Random.nextInt(0, quoteList.size - 1)]
        instanceQuotes.quote = "\"" + instanceQuotes.quote + "\""
        quoteView.text =  instanceQuotes.quote
        authorView.text = instanceQuotes.author
    }

    private fun getScreenWidth(): String {
        return Resources.getSystem().displayMetrics.widthPixels.toString()
    }

    private fun getScreenHeight(): String {
        return Resources.getSystem().displayMetrics.heightPixels.toString()
    }
}