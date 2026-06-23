package com.app.adhyatmah.utils.lang

import android.content.Context
import androidx.preference.PreferenceManager
import java.util.*
import kotlin.let
import kotlin.run
import kotlin.text.split
import androidx.core.content.edit

object LanguageSetting {

    private const val KEY_CURRENT_LANGUAGE = "settingTextLanguage"

    private const val KEY_DEFAULT_LANGUAGE = "default_key"

    @JvmStatic
    fun setDefaultLanguage(context: Context, locale: Locale) {
        setPreference(context, KEY_DEFAULT_LANGUAGE, locale.toString())
    }

    @JvmStatic
    fun getDefaultLanguage(context: Context): Locale =
        getPreference(context, KEY_DEFAULT_LANGUAGE)?.let { locale: String ->
            val info = locale.split("_")
            when (info.size) {
                1 -> Locale(info[0])
                2 -> Locale(info[0], info[1])
                else -> Locale.ENGLISH
            }
        } ?: run {
            Locale.ENGLISH
        }

    @JvmStatic
    fun setLanguage(context: Context, locale: Locale) {
        Locale.setDefault(locale)
        setPreference(context, KEY_CURRENT_LANGUAGE, locale.toString())
    }

    @JvmStatic
    fun getLanguage(context: Context): Locale? =
        getPreference(context, KEY_CURRENT_LANGUAGE)?.let { locale: String ->
            val info = locale.split("_")
            when (info.size) {
                1 -> Locale(info[0])
                2 -> Locale(info[0], info[1])
                else -> null
            }
        } ?: run {
            null
        }

    fun getLanguageWithDefault(context: Context, default: Locale): Locale {
        return getLanguage(context) ?: run {
            setLanguage(context, default)
            default
        }
    }

    private fun setPreference(context: Context, key: String, value: String) {
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context /* Activity context */)
        sharedPreferences.edit {
            putString(key, value)
        }
    }

    private fun getPreference(context: Context, key: String, default: String? = null): String? {
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)
        val str = sharedPreferences!!.getString(KEY_CURRENT_LANGUAGE, "en")
        println(str)
        return str
    }
}