package ru.zuma.unicornclub.model

import com.google.gson.annotations.SerializedName

class Unicorn (
    var date: String? = null,
    @SerializedName("url") var url: String? = null
)