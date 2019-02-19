package ru.zuma.unicornclub

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.experimental.Job
import java.util.concurrent.ArrayBlockingQueue

class ImageGalleryAdapter(private val mContext: Activity,
                          private val mUnicornImages: ArrayList<UnicornImage>) : RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>() {

    private var imageLoadJob: Job? = null
    private val queue = ArrayBlockingQueue<UnicornImage>(20)
    private val grayRect = mContext.resources.getDrawable(R.drawable.rect_gray)
    private lateinit var imageView: ImageView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageGalleryAdapter.MyViewHolder {

        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.item_collection, parent, false)
        return MyViewHolder(photoView)
    }

    override fun onBindViewHolder(holder: ImageGalleryAdapter.MyViewHolder, position: Int) {
        val photo = mUnicornImages.get(position).copy()
        imageView = holder.mPhotoImageView
        holder.tvDate.text = String.format("%02d/%02d", photo.month, photo.dayOfMonth)

        if (photo.isKnown) {
            loadImageAsync(photo, imageView)
        } else {
            imageView.setImageDrawable(grayRect)
        }
    }

    fun loadImageAsync(photo: UnicornImage, imageView: ImageView) {
        Glide.with(mContext)
            .load(Backend.imageURL(photo))
            .asBitmap()
            .placeholder(grayRect)
            .listener(object: RequestListener<String, Bitmap> {
                override fun onException(e: Exception?, model: String?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    Log.e(this@ImageGalleryAdapter.javaClass.simpleName, e?.message ?: "load failed")
                    queue.add(photo)
                    return false
                }

                override fun onResourceReady(resource: Bitmap?, model: String?, target: Target<Bitmap>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                    Log.d(this@ImageGalleryAdapter.javaClass.simpleName,
                            "Image " + String.format("%02d/%02d", photo.month, photo.dayOfMonth) + " loaded")
                    return false
                }

            })
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        imageLoadJob = launchPrintThrowable {
            while (true) {
                val photo = queue.take()
                mContext.runOnUiThread {
                    loadImageAsync(photo, imageView)
                }
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        imageLoadJob?.cancel()
    }

    override fun getItemCount(): Int {
        return mUnicornImages.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var mPhotoImageView: ImageView
        var tvDate: TextView

        init {
            mPhotoImageView = itemView.findViewById(R.id.ivPhoto) as ImageView
            tvDate = itemView.findViewById(R.id.tvDate) as TextView
            itemView.setOnClickListener(this)
        }

        private var onClickTime: Long = 0
        private var onClickTimeout: Long = 1000

        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION && System.currentTimeMillis() > onClickTime) {
                onClickTime = System.currentTimeMillis() + onClickTimeout

                if (mPhotoImageView.drawable == grayRect) return

                val intent = Intent(mContext, UnicornImageActivity::class.java)
                intent.putExtra(UnicornImageActivity.CURRENT_UNICORN_POS, position)
                intent.putParcelableArrayListExtra(UnicornImageActivity.UNICORN_IMAGES_LIST, mUnicornImages)
                startActivity(mContext, intent, null)
            }
        }
    }
}