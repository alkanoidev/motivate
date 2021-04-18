package com.example.motivate.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.motivate.Model
import com.example.motivate.R
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

    private var thread = Thread()

    private lateinit var list:MutableList<Model>
    private lateinit var instance: Model

    private val TAG: String = MainActivity::class.java.simpleName

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*val background: RelativeLayout = findViewById(R.id.background)
        val res = resources
        val myImages = res.obtainTypedArray(R.array.img)
        val drawableID = myImages.getResourceId(Random.nextInt(0,myImages.length()),-1)
        background.setBackgroundResource(drawableID)*/

        quoteView=findViewById(R.id.quote)
        authorView=findViewById(R.id.author)

        thread = Thread(Runnable {
            val url:URL? = try {
                URL("https://type.fit/api/quotes")
            }catch (e: MalformedURLException){
                Log.d("Exception", e.toString())
                null
            }
            url?.getString()?.apply {
                list = parseJson(this@apply)

            }
            runOnUiThread {
                quoteView.text = getString(R.string.presetquote)
                authorView.text = getString(R.string.presetauthor)
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
        instance.quote = "''" + instance.quote + "''"
        quoteView.text =  instance.quote
        authorView.text = instance.author

        Log.d(TAG,"$instance.quote")
        Log.d(TAG,"$instance.author")
    }
}

