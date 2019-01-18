package ru.zuma.unicornclub.model

import com.google.gson.annotations.SerializedName

class StatusResponse (
    var status: Int? = null,
    @SerializedName("error_message") var errorMessage: String? = null
)