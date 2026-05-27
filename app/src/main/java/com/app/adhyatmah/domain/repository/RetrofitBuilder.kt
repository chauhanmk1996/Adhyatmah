package com.app.adhyatmah.domain.repository

import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.data.preferences.SELECTED_LANGUAGE_CODE
import com.app.adhyatmah.utils.MyApplication
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitBuilder {
      // private const val BASE_URL = "http://13.233.120.9:4040/shopify/"
    //private const val BASE_URL = "https://adhyatmah-bharat-web-1.onrender.com/shopify/"


//    private const val BASE_URL = "http://43.204.229.149:4444/shopify/"
    private const val BASE_URL = "https://api.adhyatmah.com/api/"


    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .addInterceptor(provideHeaderInterceptor())  // Attach header interceptor
        .addInterceptor(provideLoggingInterceptor()) // Attach logging interceptor
        .addInterceptor(TokenInterceptor())          // Attach token interceptor (new)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    private fun provideHeaderInterceptor(): Interceptor {
//        val selectedLangName = Preferences.getStringPreference(MyApplication.context, SELECTED_LANGUAGE)
//        val langCode = if (selectedLangName == "Hindi") "hi" else "en"  // default to English

        return Interceptor { chain ->
            val token = Preferences.getStringPreference(MyApplication.context, ACCESS_TOKEN)

            val request = chain.request().newBuilder()
            request.addHeader("Accept", "application/json")
            request.addHeader("Content-Type", "application/json")
            request.addHeader("lang", Preferences.getStringPreference(MyApplication.context, SELECTED_LANGUAGE_CODE) ?: "en")

            if (!token.isNullOrEmpty()) {
                request.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(request.build())
        }
    }

    private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}





