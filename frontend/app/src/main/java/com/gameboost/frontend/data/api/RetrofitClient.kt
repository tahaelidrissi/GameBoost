package com.gameboost.frontend.data.api

import android.content.Context
import com.gameboost.frontend.BuildConfig
import com.gameboost.frontend.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var instance: Retrofit? = null

    fun getInstance(context: Context): Retrofit {
        if (instance == null) {
            val tokenManager = TokenManager(context)
            val httpClient = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(AuthInterceptor(tokenManager))
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()

            instance = Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return instance!!
    }

    class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val token = tokenManager.getToken()

            // Si pas de token, on continue sans l'ajouter (pour login/register)
            if (token == null) {
                return chain.proceed(originalRequest)
            }

            // Ajouter le Bearer token
            val requestWithToken = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .build()

            return chain.proceed(requestWithToken)
        }
    }
}
