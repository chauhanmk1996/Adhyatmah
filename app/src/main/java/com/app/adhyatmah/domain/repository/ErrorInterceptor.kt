package com.app.adhyatmah.domain.repository

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == 400) {
            val errorBody = response.peekBody(Long.MAX_VALUE).string()
            try {
                val jsonObject = JSONObject(errorBody)
                val message = jsonObject.optString("message", "Unknown error")
                Log.e("API_ERROR_400", "Message: $message")
            } catch (e: Exception) {
                Log.e("API_ERROR_400", "Failed to parse error JSON: ${e.message}")
            }
        }

        return response
    }
}
