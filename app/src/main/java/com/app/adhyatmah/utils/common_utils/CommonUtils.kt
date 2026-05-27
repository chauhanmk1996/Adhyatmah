package com.app.adhyatmah.utils.common_utils

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.LocaleList
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.app.adhyatmah.R
import com.app.panditji.data.model.app_language.AppLanguage
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class CommonUtils {
    companion object {

        private const val PREF_LANGUAGE = "selected_language"
        val languageList = listOf(
            AppLanguage("English", "en"),
            AppLanguage("Hindi", "hi"),
            AppLanguage("Marathi", "mr"),
            AppLanguage("Bengali", "bn"),
            AppLanguage("Gujarati", "gu"),
            AppLanguage("Odia", "or"),
            AppLanguage("Tamil", "ta"),
            AppLanguage("Telugu", "te"),
            AppLanguage("Kannada", "kn"),
            AppLanguage("Malayalam", "ml")
        )

        fun setLocale(context: Context, languageCode: String): Context {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val config = Configuration(context.resources.configuration)

            // This is the fix for API 24+ — use LocaleList
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)   // ← setLocales not setLocale
            config.setLayoutDirection(locale)

            return context.createConfigurationContext(config)
        }

        @TargetApi(Build.VERSION_CODES.N)
        private fun updateResources(context: Context, languageCode: String): Context {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val config = context.resources.configuration
            config.setLocale(locale)
            config.setLayoutDirection(locale)

            return context.createConfigurationContext(config)
        }

        @Suppress("DEPRECATION")
        private fun updateResourcesLegacy(context: Context, languageCode: String): Context {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val resources = context.resources
            val config = resources.configuration
            config.locale = locale
            config.setLayoutDirection(locale)

            resources.updateConfiguration(config, resources.displayMetrics)
            return context
        }

        // Call this in your Application class or BaseActivity
        fun onAttach(context: Context): Context {
            val lang = getPersistedLanguage(context) ?: "en"
            return setLocale(context, lang)
        }

        private fun getPersistedLanguage(context: Context): String? {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            Log.i("TAG", "getPersistedLanguage: "+prefs.getString(PREF_LANGUAGE, null))
            return prefs.getString(PREF_LANGUAGE, null)
        }
//        fun getLocalisedString(id:Int, locale: Locale, configuration: Configuration, application:Application):String{
//            configuration.setLocale(locale)
//            val resources = application.createConfigurationContext(configuration).resources
//            return resources.getString(id)
//        }
        fun updateBaseContextLocale(context: Context, language: String): Context {
            val locale = Locale(language)
            Locale.setDefault(locale)

            val config = context.resources.configuration
            config.setLocale(locale)
            config.setLayoutDirection(locale)

            return context.createConfigurationContext(config)
        }


        fun formatDate(inputDate: String?, inputFormat: String): String {
            if (inputDate.isNullOrEmpty()) return ""
            return try {
                // Input format (with 'Z' indicating UTC timezone)
                val inputFormat =
                    SimpleDateFormat(inputFormat, Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")

                // Output format (e.g. "17 Oct, 2025")
                val outputFormat = SimpleDateFormat("dd MMM, yyyy HH:mm a", Locale.getDefault())

                val date = inputFormat.parse(inputDate)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }

        fun dpToPx(context: Context, dp: Int): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }

        fun pxToDp(context: Context, px: Int): Int {
            return (px / context.resources.displayMetrics.density).toInt()
        }


        fun showCustomAlertDialog(
            context: Context,
            title: String,
            message: String,
            positiveButtonText: String = "OK",
            negativeButtonText: String = "Cancel",
            positiveButtonAction: (() -> Unit)? = null,
            negativeButtonAction: (() -> Unit)? = null,

        ): AlertDialog {
            // Inflate the custom layout
            val customLayout: View = LayoutInflater.from(context)
                .inflate(R.layout.custom_alert_dialog, null)

            // Get references to the views in the custom layout
            val dialogTitle: TextView = customLayout.findViewById(R.id.dialogTitle)
            val dialogMessage: TextView = customLayout.findViewById(R.id.dialogMessage)
            val positiveButton: Button = customLayout.findViewById(R.id.positiveButton)
            val negativeButton: Button = customLayout.findViewById(R.id.negativeButton)

            // Set title and message dynamically
            dialogTitle.text = title
            dialogMessage.text = message

            // Set button texts
            positiveButton.text = positiveButtonText
            negativeButton.text = negativeButtonText

            // Set button click listeners
            positiveButton.setOnClickListener {
                positiveButtonAction?.invoke() // Call the positive button action if provided
            }

            negativeButton.setOnClickListener {
                negativeButtonAction?.invoke() // Call the negative button action if provided
            }

            // Create the AlertDialog
            val builder = AlertDialog.Builder(context)
            builder.setView(customLayout)

            // Create and show the AlertDialog
            val dialog = builder.create()
            dialog.show()

            return dialog
        }

        fun showPincodeDialog(
            context: Context,
            title: String,
            message: String,
            positiveButtonText: String = "OK",
            negativeButtonText: String = "Cancel",
            positiveButtonAction: (() -> Unit)? = null,
            negativeButtonAction: (() -> Unit)? = null,

            ): AlertDialog {
            // Inflate the custom layout
            val customLayout: View = LayoutInflater.from(context)
                .inflate(R.layout.pincode_popup, null)

            // Get references to the views in the custom layout
            val dialogTitle: TextView = customLayout.findViewById(R.id.dialogTitle)
            val dialogMessage: TextView = customLayout.findViewById(R.id.dialogMessage)
            val positiveButton: Button = customLayout.findViewById(R.id.positiveButton)
            val negativeButton: Button = customLayout.findViewById(R.id.negativeButton)

            // Set title and message dynamically
            dialogTitle.text = title
            dialogMessage.text = message

            // Set button texts
            positiveButton.text = positiveButtonText
            negativeButton.text = negativeButtonText

            // Set button click listeners
            positiveButton.setOnClickListener {
                positiveButtonAction?.invoke() // Call the positive button action if provided
            }

            negativeButton.setOnClickListener {
                negativeButtonAction?.invoke() // Call the negative button action if provided
            }

            // Create the AlertDialog
            val builder = AlertDialog.Builder(context)
            builder.setView(customLayout)

            // Create and show the AlertDialog
            val dialog = builder.create()
            dialog.show()

            return dialog
        }
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork ?: return false
                val actNw = connectivityManager.getNetworkCapabilities(network) ?: return false

                return actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } else {
                val networkInfo = connectivityManager.activeNetworkInfo ?: return false
                return networkInfo.isConnected
            }
        }
    }
}
