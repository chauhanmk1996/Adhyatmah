package com.app.adhyatmah.utils.lang

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.*

object LocalizationUtility {
    fun applyLocalizationContext(baseContext: Context): Context {
        val baseLocale = getLocaleFromConfiguration(baseContext.resources.configuration)
        val currentLocale = LanguageSetting.getLanguageWithDefault(baseContext, LanguageSetting.getDefaultLanguage(baseContext))
        if (!baseLocale.toString().equals(currentLocale.toString(), ignoreCase = true)) {
            val context = LocalizationContext(baseContext)

            return when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    val configuration = Configuration()
                    configuration.setLocale(currentLocale)
                    val localeList = LocaleList(currentLocale)
                    LocaleList.setDefault(localeList)
                    configuration.setLocales(localeList)
                    context.createConfigurationContext(configuration)
                }

                else -> {
                    val configuration = Configuration()
                    configuration.setLocale(currentLocale)
                    context.createConfigurationContext(configuration)
                }
            }
        } else {
            return baseContext
        }
    }

    @Suppress("DEPRECATION")
    fun getLocaleFromConfiguration(configuration: Configuration): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            configuration.locales.get(0)
        } else {
            configuration.locale
        }
    }
}