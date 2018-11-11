package ru.zuma.unicornclub

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var sftpManager: SFTPManager
    var image: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.unicorn_club)

        sftpManager = SFTPManager()
        loadImage()

        btAnimation.setOnClickListener {
            val bitmap = image
            if (bitmap == null) {
                loadImage()
            } else {
                updateDailyImage(bitmap)
            }
        }
    }

    private fun loadImage() {
        val timeOfDailyImageUpdate = loadTimeOfDailyImageUpdate(this)
        sftpManager.loadDailyImageIfUpdated(timeOfDailyImageUpdate, onImage = { img, time ->
            Log.d(this@MainActivity.javaClass.simpleName, "Daily image loaded from SFTP")
            storeTimeOfDailyImageUpdate(this@MainActivity, time)
            storeDailyImage(this@MainActivity, img)
            image = img
            runOnUiThread {
                updateDailyImage(img)
            }
        }, onNothingUpdate = {
            val bitmap = loadDailyImage(this@MainActivity)
            image = bitmap
            runOnUiThread {
                if (bitmap != null) {
                    Log.d(this@MainActivity.javaClass.simpleName, "Daily image loaded from file system")
                    updateDailyImage(bitmap)
                } else {
                    Log.e(this@MainActivity.javaClass.simpleName, "Daily image not loaded")
                    toast("Похоже на сервер забыли положить единорожка:(")
                }
            }
        })
    }

    private fun updateDailyImage(img: Bitmap) {
        ivDailyImage.setImageBitmap(img)
        val animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.daily_image_appear)
        ivDailyImage.startAnimation(animation)
    }

    private fun runOnUiThread(context: Activity? = this@MainActivity, action: () -> Unit) {
        context?.runOnUiThread(action)
    }
}
