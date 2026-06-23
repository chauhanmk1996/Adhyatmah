package com.app.adhyatmah.utils.common_utils

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.LocaleList
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
        val languageList: ArrayList<AppLanguage> = arrayListOf(
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

            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
            config.setLayoutDirection(locale)

            return context.createConfigurationContext(config)
        }

        fun formatDate(inputDate: String?, inputFormat: String): String {
            if (inputDate.isNullOrEmpty()) return ""
            return try {
                val inputFormat =
                    SimpleDateFormat(inputFormat, Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")

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

        fun showCustomAlertDialog(
            context: Context,
            title: String,
            message: String,
            positiveButtonText: String = "OK",
            negativeButtonText: String = "Cancel",
            positiveButtonAction: (() -> Unit)? = null,
            negativeButtonAction: (() -> Unit)? = null,

            ): AlertDialog {
            val customLayout: View = LayoutInflater.from(context)
                .inflate(R.layout.custom_alert_dialog, null)

            val dialogTitle: TextView = customLayout.findViewById(R.id.dialogTitle)
            val dialogMessage: TextView = customLayout.findViewById(R.id.dialogMessage)
            val positiveButton: Button = customLayout.findViewById(R.id.positiveButton)
            val negativeButton: Button = customLayout.findViewById(R.id.negativeButton)

            dialogTitle.text = title
            dialogMessage.text = message

            positiveButton.text = positiveButtonText
            negativeButton.text = negativeButtonText

            positiveButton.setOnClickListener {
                positiveButtonAction?.invoke()
            }

            negativeButton.setOnClickListener {
                negativeButtonAction?.invoke()
            }

            val builder = AlertDialog.Builder(context)
            builder.setView(customLayout)

            val dialog = builder.create()
            dialog.show()

            return dialog
        }

        fun showPinCodeDialog(
            context: Context,
            title: String,
            message: String,
            positiveButtonText: String = "OK",
            negativeButtonText: String = "Cancel",
            positiveButtonAction: (() -> Unit)? = null,
            negativeButtonAction: (() -> Unit)? = null,

            ): AlertDialog {
            val customLayout: View = LayoutInflater.from(context)
                .inflate(R.layout.pincode_popup, null)

            val dialogTitle: TextView = customLayout.findViewById(R.id.dialogTitle)
            val dialogMessage: TextView = customLayout.findViewById(R.id.dialogMessage)
            val positiveButton: Button = customLayout.findViewById(R.id.positiveButton)
            val negativeButton: Button = customLayout.findViewById(R.id.negativeButton)

            dialogTitle.text = title
            dialogMessage.text = message

            positiveButton.text = positiveButtonText
            negativeButton.text = negativeButtonText

            positiveButton.setOnClickListener {
                positiveButtonAction?.invoke()
            }

            negativeButton.setOnClickListener {
                negativeButtonAction?.invoke()
            }

            val builder = AlertDialog.Builder(context)
            builder.setView(customLayout)
            val dialog = builder.create()
            dialog.show()

            return dialog
        }

        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val network = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(network) ?: return false

            return actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        }
    }
}