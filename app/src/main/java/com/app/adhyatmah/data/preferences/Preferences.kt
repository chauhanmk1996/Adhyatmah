package com.app.adhyatmah.data.preferences

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

object Preferences {
    fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(TAG, Context.MODE_PRIVATE)
    }

    fun removeAllPreferencesExcept(context: Context, excludeKeys: List<String>) {
        val settings = getSharedPreferences(context)
        val allEntries = settings.all

        settings.edit {
            for (entry in allEntries.entries) {
                if (!excludeKeys.contains(entry.key)) {
                    remove(entry.key) // Remove keys not in the exclusion list
                }
            }
        } // Apply changes
    }

    inline fun <reified T> setCustomModelPreference(context: Context?, key: String?, value: T?) {
        val settings = getSharedPreferences(context!!)
        settings.edit {
            val jsonString = Gson().toJson(value)
            putString(key, jsonString)
        }
    }

    inline fun <reified T> getCustomModelPreference(context: Context?, key: String?): T? {
        val settings = getSharedPreferences(context!!)
        val jsonString = settings.getString(key, null)
        return if (jsonString.isNullOrEmpty()) {
            null
        } else {
            Gson().fromJson(jsonString, object : TypeToken<T>() {}.type)
        }
    }

    fun setStringPreference(context: Context?, key: String?, value: String?) {
        val settings: SharedPreferences = getSharedPreferences(context!!)
        settings.edit(commit = true) {
            putString(key, value)
        }
    }

    fun getStringPreference(context: Context?, key: String?): String? {
        val pref: SharedPreferences = getSharedPreferences(context!!)
        return pref.getString(key, "")
    }
}