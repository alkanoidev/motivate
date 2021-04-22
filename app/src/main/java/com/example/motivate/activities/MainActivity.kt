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
import com.example.motivate.Model
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
    private var randomImageURL:String = "https://source.unsplash.com/collection/220381/"

    private lateinit var list:MutableList<Model>
    private lateinit var instance: Model

    private val TAG: String = MainActivity::class.java.simpleName

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        randomImageURL=randomImageURL.plus(getScreenWidth()).plus("x").plus(getScreenHeight())
        Log.d(TAG,randomImageURL)

        imageView = findViewById(R.id.imageView)
        Glide.with(context).load(randomImageURL).into(imageView);

        quoteView = findViewById(R.id.quote)
        authorView = findViewById(R.id.author)

        thread = Thread(Runnable {
            val url:URL? = try {
                URL(quotesApiURL)
            }catch (e: MalformedURLException){
                Log.d("Exception", e.toString())
                null
            }
                url?.getString()?.apply {
                list = parseJson(this@apply)
            }
            runOnUiThread {

            }
        })
        thread.start()

    }

    private fun URL.getString(): String? {
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

    private fun parseJson(data:String): MutableList<Model> {
        val list:MutableList<Model> = Gson().fromJson<MutableList<Model>>(data)
        list.forEach{
            if(it.author==null){
                it.author="Unknown"
            }
        }
        return list
    }

    inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object : TypeToken<T>() {}.type)

    fun update(view: View) {
        quoteView=findViewById(R.id.quote)
        authorView=findViewById(R.id.author)

        instance = list[Random.nextInt(0, list.size - 1)]
        instance.quote = "\"" + instance.quote + "\""
        quoteView.text =  instance.quote
        authorView.text = instance.author

//        Log.d(TAG,"$instance.quote")
//        Log.d(TAG,"$instance.author")
    }

    private fun getScreenWidth(): String {
        return Resources.getSystem().displayMetrics.widthPixels.toString()
    }

    private fun getScreenHeight(): String {
        return Resources.getSystem().displayMetrics.heightPixels.toString()
    }
}

