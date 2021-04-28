package com.example.motivate.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.motivate.R
import com.example.motivate.activities.SplashScreen.Companion.context
import com.example.motivate.models.ImagesModel
import com.example.motivate.models.QuotesModel
import com.example.motivate.util.Utils
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
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
    private var tools: Utils = Utils()

    private val TAG: String = MainActivity::class.java.simpleName

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        quoteView = findViewById(R.id.quote)
        authorView = findViewById(R.id.author)
        imageView = findViewById(R.id.imageView)

        /*thread = Thread(Runnable {
            val url:URL? = try {
                URL(quotesApiURL)
            }catch (e: MalformedURLException){
                Log.d("Exception", e.toString())
                null
            }
            url?.getString()?.apply {
                quoteList = tools.parseJsonData(this@apply)
            }
            val url1: URL? = try {
                URL(imagesURL)
            }catch (e: MalformedURLException){
                Log.d("Exception", e.toString())
                null
            }
            url1?.getString()?.apply {
                imageList = tools.parseJsonImage(this@apply)
            }

            runOnUiThread {
                instanceImages = imageList[Random.nextInt(0, imageList.size - 1)]
                instanceImages.download_url = "https://picsum.photos/id/${instanceImages.id}/${tools.getScreenWidth()}/${tools.getScreenHeight()}/?blur=5"
                Glide.with(context).load(instanceImages.download_url).into(imageView)

            }
        })
        thread.start()*/

        GlobalScope.launch(Dispatchers.IO) {
            process()
            setImage()
        }


    }
    private suspend fun process() = coroutineScope {
        quoteList = async { tools.getQuotes() }.await()
        imageList = async{ tools.getImages() }.await()
    }

    private suspend fun setImage() = withContext(Main){
        instanceImages = imageList[Random.nextInt(0, imageList.size - 1)] //not initialized
        instanceImages.download_url = "https://picsum.photos/id/" +
                                      "${instanceImages.id}/${tools.getScreenWidth()}/" +
                                      "${tools.getScreenHeight()}/?blur=5"
        Glide.with(context).load(instanceImages.download_url).into(imageView)
    }

    fun update(view: View) {
        quoteView=findViewById(R.id.quote)
        authorView=findViewById(R.id.author)

        instanceQuotes = quoteList[Random.nextInt(0, quoteList.size - 1)]
        instanceQuotes.quote = "\"" + instanceQuotes.quote + "\""
        quoteView.text =  instanceQuotes.quote
        authorView.text = instanceQuotes.author
    }

}