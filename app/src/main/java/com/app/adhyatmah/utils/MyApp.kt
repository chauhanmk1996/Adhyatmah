package com.app.adhyatmah.utils

import android.app.Application



class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
       // FirebaseApp.initializeApp(this)
    }
}