package com.app.adhyatmah.domain.repository

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.FCM_TOKEN
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.presentation.ui.activity.LoginActivity
import com.app.adhyatmah.utils.MyApplication
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // Read response body as string
        val responseBodyString = response.peekBody(Long.MAX_VALUE).string()

        // Check if API returns 401 inside response body
        Log.i("TAG", "intercept: "+responseBodyString)
        if (responseBodyString.contains("\"code\":401")||responseBodyString.contains("\"code\":422")) {
            synchronized(this) {
                try {
                    val json = JSONObject(responseBodyString)
                    val message = json.optString("message", "Unauthorized access")

                    Handler(Looper.getMainLooper()).post {
                        MyApplication.currentActivity?.let {
                            Log.d("TAG", "inteffgdfgdfgdfgrcept: $message ")
                            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
                        } ?: Log.e("TokenInterceptor", "No active activity to show Toast")
                    }

                   /* Handler(Looper.getMainLooper()).post {
                        Toast.makeText(com.example.ronthompson.utils.MyApplication.context, message, Toast.LENGTH_LONG).show()
                    }*/

                    Log.e("TokenInterceptor", "401 Error Message: $message")

                    // Optional: Trigger logout if needed
                     logout()

                } catch (e: Exception) {
                    Log.e("TokenInterceptor", "JSON parsing failed: ${e.message}")
                }
            }
        }
           /* synchronized(this) {
                val newToken = runBlocking {
                    try {

                    } catch (e: Exception) {
                        null
                    }
                }

              *//* if (!newToken.isNullOrEmpty()) {
                    Log.d("TokenInterceptor", "New Token: $newToken")
                    var data = Preferences.getCustomModelPreference<LoginUserData>(
                        com.example.ronthompson.utils.MyApplication.context,
                        USER_DETAILS
                    )
                    data?.token = newToken
                    // Save new token to preferences
                    Preferences.setCustomModelPreference(com.example.ronthompson.utils.MyApplication.context, USER_DETAILS, data)

                    // Retry the original request with new token
                    val newRequest = request.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()

                    return chain.proceed(newRequest)
                }*//*
            }*/

        else if (responseBodyString.contains("\"code\":403")) {
            Handler(Looper.getMainLooper()).post {
                val context = MyApplication.context
                val activity = MyApplication.currentActivity
                if (activity != null) {
                    // Show AlertDialog before redirecting (optional)
                    val builder = AlertDialog.Builder(activity)
                        .setTitle("Account Inactive")
                        .setMessage("Your account is inactive. Please contact admin..")
                        .setCancelable(false)
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()

                            // Clear user data
                          //  Preferences.clearAllPreferences(context)

                            // Redirect to Onboarding screen
                            //logout()
                        }

                    val dialog = builder.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                        activity.getColor(R.color.black)
                    )

                } else {
                    Log.e("TokenInterceptor", "No active activity to show dialog or redirect.")
                }
            }
        }
        else if (responseBodyString.contains("\"code\":404")) {
            Handler(Looper.getMainLooper()).post {

                if (DialogState.isErrorDialogVisible) return@post  // ⛔ Prevent new dialogs

                val activity = MyApplication.currentActivity
                val json = JSONObject(responseBodyString)
                val message = json.optString("message", "Something went wrong")

                if (activity != null) {

                    val builder = AlertDialog.Builder(activity)
                        .setTitle(R.string.app_name)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                            DialogState.isErrorDialogVisible = false  // Reset on close
                        }

                    val dialog = builder.create()

                    dialog.setOnDismissListener {
                        DialogState.isErrorDialogVisible = false // Extra safety
                    }

                    DialogState.isErrorDialogVisible = true
                    dialog.show()

                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                        activity.getColor(R.color.black)
                    )

                }
            }
        }

        /* else if (responseBodyString.contains("\"code\":422")) {
             Handler(Looper.getMainLooper()).post {
                 val context = MyApplication.context
                 val activity = MyApplication.currentActivity
                 if (activity != null) {
                     // Show AlertDialog before redirecting (optional)
                     val builder = AlertDialog.Builder(activity)
                         .setTitle("Account Inactive")
                         .setMessage("Your account is inactive. Please contact admin..")
                         .setCancelable(false)
                         .setPositiveButton("OK") { dialog, _ ->
                             dialog.dismiss()

                             // Clear user data
                             //  Preferences.clearAllPreferences(context)

                             // Redirect to Onboarding screen
                             //logout()
                         }

                     val dialog = builder.create()
                     dialog.show()
                     dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                         activity.getColor(R.color.black)
                     )

                 } else {
                     Log.e("TokenInterceptor", "No active activity to show dialog or redirect.")
                 }
             }
         }
        */
        if (responseBodyString.contains("\"code\":400")) {
            try {
                val json = JSONObject(responseBodyString)
                val message = json.optString("message", "Something went wrong")

                // Run UI work on the main thread
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(MyApplication.context, message, Toast.LENGTH_LONG).show()
                }

                Log.d("TAG", "Intercepted 400 Error: $message")
            } catch (e: Exception) {
                Log.e("TokenInterceptor", "Error parsing 400 response: ${e.message}")
            }
        }


        return response
    }
    private fun logout() {
        Preferences.removeAllPreferencesExcept(MyApplication.context, listOf(FCM_TOKEN))

        val intent = Intent(MyApplication.context, LoginActivity::class.java)
        intent.putExtra("screen", "login") // <--- Tell LoginActivity to show login
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        MyApplication.context.startActivity(intent)

    }
}
object DialogState {
    var isErrorDialogVisible = false
}

