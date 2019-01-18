package ru.zuma.unicornclub

import android.util.Log
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.zuma.unicornclub.model.DailyUnicorn
import ru.zuma.unicornclub.model.StatusResponse
import ru.zuma.unicornclub.model.User
import java.lang.IndexOutOfBoundsException


interface BackendApi {
    @GET("unicorn/daily")
    fun getDailyUnicorn(): Call<DailyUnicorn>

    @POST("unicorn/collection")
    fun saveDailyUnicornToCollection(): Call<ResponseBody>

    @POST("unicorn/auth/register")
    fun register(@Body user: User): Call<StatusResponse>

    @POST("unicorn/auth/login")
    fun login(@Body user: User): Call<ResponseBody>
}

object Backend {
    val urlBase = "http://zzuummaa.sytes.net:8070/"
    val urlUnicornImagesBase = urlBase + "fileserver/unicorn/images/"
    var api: BackendApi
    val auth: UnicornAuthenticator = UnicornAuthenticator()

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .authenticator(auth)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(urlBase)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        api = retrofit.create(BackendApi::class.java)
    }

    fun authByAndroidId(androidId: String): String? {
        try {
            val user = User(androidId)
            val loginCall = Backend.api.login(user)
            val loginResp = loginCall.execute()

            if (loginResp.code() == 200) {
                val token = loginResp.headers()[Backend.auth.header]
                if (token != null) {
                    Backend.auth.token = token
                    return token
                } else {
                    Log.e(javaClass.simpleName, "Login response isn't contains auth header")
                    return null
                }
            } else if (loginResp.code() != 203) {
                Log.e(javaClass.simpleName, "Request from ${loginCall.request().url()} return code ${loginResp.code()}")
                return null
            }

            val registerCall = Backend.api.register(user)
            val registerResp = registerCall.execute()

            if (registerResp.code() == 200) {
                val token = registerResp.headers()[Backend.auth.header]
                if (token != null) {
                    Backend.auth.token = token
                    return token
                } else {
                    Log.e(javaClass.simpleName, "Register response isn't contains auth header")
                }
            } else {
                Log.e(javaClass.simpleName, "Request from ${registerCall.request().url()} return code ${registerResp.code()}")
                return null
            }
        } catch (e: Throwable) {
            Log.e(javaClass.simpleName, e.message ?: "")
        }

        return null
    }

    fun imageURL(photo: UnicornImage) = urlUnicornImagesBase + String.format("%02d%02d", photo.month, photo.dayOfMonth) + ".jpg"
}

class UnicornAuthenticator: Authenticator {
    var token: String? = null
        set(value) {
            if (value != null) {
                field = value
                authMutex.unlock()
                isTokenInit = true
            }
        }
    var isTokenInit = false
    var header: String = "WWW-Authenticate"

    private val authMutex = Mutex(true)

    override fun authenticate(route: Route?, response: Response): Request? {
        val request = response.request()
        if (request.header(header) != null)  {
            return null
        }

        var requestToken =  response.request().header(header)
        if (requestToken != null) {
            try {
                requestToken = requestToken.substring(requestToken.indexOf(' ') + 1)
            } catch (e: IndexOutOfBoundsException) {
                Log.e(javaClass.simpleName, e.message, e)
                return null
            }

            if (token == requestToken) return null
        }

        token?.let {
            return request.newBuilder()
                    .header(header, "Basic $it")
                    .build()
        }

        return request
    }

    suspend fun<T> waitAuthBefore(runnable: suspend () -> T): T {
        if (!isTokenInit) {
            authMutex.lock()
            authMutex.unlock()
        }
        return runnable.invoke()
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