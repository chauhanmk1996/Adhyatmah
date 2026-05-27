package com.app.adhyatmah.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.preference.PreferenceManager
import co.paystack.android.PaystackSdk
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.lang.LocalizationApplication
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

class MyApplication : Application(), LifecycleObserver {
    /*override fun attachBaseContext(base: Context) {
        super.attachBaseContext(CommonUtils.onAttach(base))
    }*/
    companion object {
        private var appContext: Context? = null
        val context: Context
            get() = appContext
                ?: throw IllegalStateException("Context not initialized yet.")

        var currentActivity: Activity? = null
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
       // PaystackSdk.initialize(applicationContext)
        PaystackSdk.initialize(applicationContext);

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Log.d("MyApplication", "Activity Created: ${activity.localClassName}")

            }
            override fun onActivityStarted(activity: Activity) {
                Log.d("MyApplication", "Activity Started: ${activity.localClassName}")

            }
            override fun onActivityResumed(activity: Activity) {
                currentActivity = activity
                Log.d("MyApplication", "Activity Resumed: ${activity.localClassName}")

            }

            override fun onActivityPaused(activity: Activity) {
                currentActivity = null
                Log.d("MyApplication", "Activity Paused: ${activity.localClassName}")

            }

            override fun onActivityStopped(activity: Activity) {
                Log.d("MyApplication", "Activity Stopped: ${activity.localClassName}")

            }
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                Log.d("MyApplication", "Activity Destroyed: ${activity.localClassName}")
            }
        })
    }
    override fun attachBaseContext(base: Context) {
        val code = PreferenceManager.getDefaultSharedPreferences(base)
            .getString("selected_language_code", "en") ?: "en"
        super.attachBaseContext(CommonUtils.setLocale(base, code))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val code = PreferenceManager.getDefaultSharedPreferences(this)
            .getString("selected_language_code", "en") ?: "en"
        CommonUtils.setLocale(this, code)
    }
    /*override fun getDefaultLanguage(): Locale {
        return Locale.ENGLISH
    }*/
}
