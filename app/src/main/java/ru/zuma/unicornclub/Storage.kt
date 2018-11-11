package ru.zuma.unicornclub

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.*


val DAILY_IMAGE_FILE_NAME = "daily.jpg"

fun storeDailyImage(context: Activity?, image: Bitmap): Boolean {
    try {
        context?.openFileOutput(DAILY_IMAGE_FILE_NAME, Context.MODE_PRIVATE).use { out ->
            return image.compress(Bitmap.CompressFormat.JPEG, 100, out) // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return false
}

fun loadDailyImage(context: Activity?): Bitmap? {
    val options = BitmapFactory.Options()
    options.inPreferredConfig = Bitmap.Config.ARGB_8888
    try {
        context?.openFileInput(DAILY_IMAGE_FILE_NAME).use {
            return BitmapFactory.decodeStream(it, null, options)
        }

    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }

    return null
}

fun storeTimeOfDailyImageUpdate(context: Activity?, time: Int) {
    val sharedPref = context?.getPreferences(Context.MODE_PRIVATE) ?: return
    with (sharedPref.edit()) {
        putInt(context.getString(R.string.daily_image_update_time), time)
        commit()
    }
}

fun loadTimeOfDailyImageUpdate(context: Activity?): Int {
    val sharedPref = context?.getPreferences(Context.MODE_PRIVATE) ?: return 0
    return sharedPref.getInt(context.getString(R.string.daily_image_update_time), 0)
}