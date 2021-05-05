package com.example.motivate.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.motivate.R
import com.example.motivate.activities.SplashScreen.Companion.context
import com.example.motivate.models.ImagesModel
import com.example.motivate.models.QuotesModel
import com.example.motivate.util.Utils
import com.google.android.material.transition.platform.MaterialSharedAxis
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private lateinit var quoteView: TextView
    private lateinit var authorView: TextView
    private lateinit var imageView: ImageView

    private lateinit var quoteList:MutableList<QuotesModel>
    private lateinit var imageList:MutableList<ImagesModel>
    private lateinit var instanceQuotes: QuotesModel
    private lateinit var instanceImages: ImagesModel
    private var tools: Utils = Utils()

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        val enter = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            addTarget(R.id.author)
            addTarget(R.id.quote)
            addTarget(R.id.next_btn)
        }
        window.enterTransition = enter
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        quoteView = findViewById(R.id.quote)
        authorView = findViewById(R.id.author)
        imageView = findViewById(R.id.imageView)

        GlobalScope.launch(Default) {
            process()
            //setImage()
            showToast()
        }
    }
    private suspend fun process() = coroutineScope {
        quoteList = async { tools.getQuotes() }.await()
        imageList = async{ tools.getImages() }.await()
    }

    private suspend fun setImage() = withContext(Main){
        instanceImages = imageList[Random.nextInt(0, imageList.size - 1)]
        instanceImages.download_url = "https://picsum.photos/id/" +
                                      "${instanceImages.id}/${tools.getScreenWidth()}/" +
                                      "${tools.getScreenHeight()}/?blur=5"
        Glide.with(context).load(instanceImages.download_url).into(imageView)
    }

    private suspend fun showToast() = withContext(Main){
        delay(4000)
        val toast = Toast.makeText(context,"Click on screen to change background!", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 50)
        toast.show()
    }

    fun update(view: View) {
        quoteView=findViewById(R.id.quote)
        authorView=findViewById(R.id.author)

        instanceQuotes = quoteList[Random.nextInt(0, quoteList.size - 1)]
        instanceQuotes.quote = "\"" + instanceQuotes.quote + "\""
        quoteView.text =  instanceQuotes.quote
        authorView.text = instanceQuotes.author
    }

    fun changeBackground(view: View) {
        instanceImages = imageList[Random.nextInt(0, imageList.size - 1)]
        instanceImages.download_url = "https://picsum.photos/id/" +
                "${instanceImages.id}/${tools.getScreenWidth()}/" +
                "${tools.getScreenHeight()}/?blur=5"
        Glide.with(context).load(instanceImages.download_url).into(imageView)
    }

}