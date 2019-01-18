package ru.zuma.unicornclub.model

import com.google.gson.annotations.SerializedName

class User (
    @SerializedName("android_id") var androidId: String? = null
)