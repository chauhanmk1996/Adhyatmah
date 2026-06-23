package com.app.adhyatmah.utils.lang

import android.content.Context
import java.util.*

class LocalizationApplicationDelegate {

    fun onConfigurationChanged(context: Context) =
        LocalizationUtility.applyLocalizationContext(context)

    fun attachBaseContext(context: Context): Context =
        LocalizationUtility.applyLocalizationContext(context)

    fun getApplicationContext(applicationContext: Context): Context =
        LocalizationUtility.applyLocalizationContext(applicationContext)

    fun setDefaultLanguage(context: Context, locale: Locale) {
        LanguageSetting.setDefaultLanguage(context, locale)
    }
}
