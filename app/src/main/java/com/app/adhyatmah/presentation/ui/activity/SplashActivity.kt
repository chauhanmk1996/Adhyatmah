package com.app.adhyatmah.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.IS_LOGIN
import com.app.adhyatmah.databinding.ActivitySplashBinding
import com.app.adhyatmah.utils.base.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val splashScope = CoroutineScope(Dispatchers.Main + Job())
    var isLogin = ""

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScope.launch {
            delay(1200) // 2 seconds delay
            isLogin = Preferences.getStringPreference(this@SplashActivity, IS_LOGIN).toString()

            if (isLogin == "1") {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, OnBoardingActivity::class.java))
            }
            finish()
        }
    }

    override fun onDestroy() {
        splashScope.cancel() // cancel coroutine to avoid memory leaks
        super.onDestroy()
    }
}