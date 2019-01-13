package ru.zuma.unicornclub

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_space_photo.*

class UnicornImageActivity : AppCompatActivity() {
    companion object {
        val EXTRA_SPACE_PHOTO = "UnicornImageActivity.SPACE_PHOTO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_space_photo)

        val photo = intent.getParcelableExtra<UnicornImage>(EXTRA_SPACE_PHOTO)

        Glide.with(this)
                .load(Backend.imageURL(photo))
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

}
