package com.app.adhyatmah.utils.lang

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import java.util.*
import kotlin.text.isNullOrEmpty

abstract class LocalizationApplication : Application() {
    private val localizationDelegate = LocalizationApplicationDelegate()

    override fun attachBaseContext(base: Context) {
        val loc = getDefaultLanguage()
        var locale = loc
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(base/* Activity context */)
        val languageCode = sharedPreferences?.getString("settingTextLanguage", "en")
        println("savedLang:${languageCode}")

        if (!languageCode.isNullOrEmpty()) {

            locale = Locale(languageCode)
        }

        localizationDelegate.setDefaultLanguage(base, locale)

        super.attachBaseContext(localizationDelegate.attachBaseContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localizationDelegate.onConfigurationChanged(this)
    }

    override fun getApplicationContext(): Context {
        return localizationDelegate.getApplicationContext(super.getApplicationContext())
    }

    abstract fun getDefaultLanguage(): Locale
}