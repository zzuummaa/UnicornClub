package ru.zuma.unicornclub

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class ImageGalleryAdapter(private val mContext: Context, private val mUnicornImages: ArrayList<UnicornImage>) : RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>() {

    private val grayRect = mContext.resources.getDrawable(R.drawable.rect_gray)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageGalleryAdapter.MyViewHolder {

        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.item_collection, parent, false)
        return MyViewHolder(photoView)
    }

    override fun onBindViewHolder(holder: ImageGalleryAdapter.MyViewHolder, position: Int) {

        val photo = mUnicornImages.get(position)
        val imageView = holder.mPhotoImageView
        holder.tvDate.text = String.format("%02d/%02d", photo.month, photo.dayOfMonth)

        if (photo.isKnown) {
            Glide.with(mContext)
                    .load(Backend.imageURL(photo))
                    .placeholder(grayRect)
                    .into(imageView)
        } else {
            imageView.setImageDrawable(grayRect)
        }

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

        override fun onClick(view: View) {

            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val spacePhoto = mUnicornImages[position]

                if (mPhotoImageView.drawable == grayRect) return

                val intent = Intent(mContext, UnicornImageActivity::class.java)
                intent.putExtra(UnicornImageActivity.EXTRA_SPACE_PHOTO, spacePhoto)
                startActivity(mContext, intent, null)
            }
        }
    }
}