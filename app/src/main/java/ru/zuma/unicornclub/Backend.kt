package ru.zuma.unicornclub

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface BackendApi {
    @GET("/fileserver/unicorn/images/{id}.jpg")
    fun getUnicornImage(@Path("id") id: String): Call<ResponseBody>

    @GET("/fileserver/unicorn/daily.jpg")
    fun getDailyUnicornImage(): Call<ResponseBody>
}

object Backend {
    var api: BackendApi

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl("http://zzuummaa.sytes.net:8070/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        api = retrofit.create(BackendApi::class.java)
    }
}

fun <T> Call<T>.unwrapCall(): T? {
    try {
        val resp = execute()

        if (resp.code() == 200) {
            return resp.body()
        } else {
            Log.w(javaClass.simpleName, "Request from ${request().url()} return code ${resp.code()}")
            return null
        }
    } catch (e: Throwable) {
        Log.e(javaClass.simpleName, e.message ?: "")
    }

    return null
}