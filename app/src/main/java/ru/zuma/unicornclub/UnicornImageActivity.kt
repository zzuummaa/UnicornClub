package ru.zuma.unicornclub

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.view.MotionEvent
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_space_photo.*

class UnicornImageActivity : AppCompatActivity() {
    companion object {
        const val CURRENT_UNICORN_POS = "UnicornImageActivity.CURRENT_UNICORN_POS"
        const val UNICORN_IMAGES_LIST = "UnicornImageActivity.UNICORN_IMAGES_LIST"
    }

    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var unicornImages: ArrayList<UnicornImage>
    private var pos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_space_photo)

        gestureDetector = GestureDetectorCompat(this, DetectSwipeGestureListener(this))

        pos = intent.getIntExtra(CURRENT_UNICORN_POS, 0)
        unicornImages = intent.getParcelableArrayListExtra(UNICORN_IMAGES_LIST)

        displayUnicorn(pos)
    }

    fun displayUnicorn(pos: Int) {
        val image = unicornImages[pos]

        Glide.with(this)
                .load(Backend.imageURL(image))
                .asBitmap()
                .listener(object : RequestListener<String, Bitmap> {

                    override fun onException(e: Exception, model: String, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Bitmap, model: String, target: Target<Bitmap>, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                        onPalette(Palette.from(resource).generate())
                        imageView.setImageBitmap(resource)

                        return false
                    }

                    fun onPalette(palette: Palette?) {
                        if (null != palette) {
                            val parent = imageView.getParent().getParent() as ViewGroup
                            parent.setBackgroundColor(palette!!.getDarkVibrantColor(Color.GRAY))
                        }
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    fun onVerticalSwipe() {
        finish()
    }

    fun onHorizontalSwipe(isToRight: Boolean) {
        if (isToRight) {
            pos = if (pos == 0) pos else pos - 1
        } else {
            pos = if (pos + 1 == unicornImages.size) pos else pos + 1
        }

        displayUnicorn(pos)
    }

    fun displayMessage(msg: String) {
        toast(msg);
    }

}
