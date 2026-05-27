package com.app.adhyatmah.presentation.ui.activity

import android.content.Context
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.data.preferences.SELECTED_LANGUAGE_CODE
import com.app.adhyatmah.databinding.ActivityLoginBinding
import com.app.adhyatmah.utils.base.BaseActivity
import com.app.adhyatmah.utils.common_utils.CommonUtils

class LoginActivity : BaseActivity<ActivityLoginBinding>() {


    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val screen = intent.getStringExtra("screen")

        // Delayed navigation so navController is ready
        binding.loginFragmentContainer.post {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.login_fragment_container) as NavHostFragment

            val navController = navHostFragment.navController

            when (screen) {
                "login" -> navController.navigate(R.id.loginFragment)
                "signup", null -> navController.navigate(R.id.signUpFragment)
            }
        }
    }

    /*override fun attachBaseContext(newBase: Context) {
        // THIS MUST BE THE VERY FIRST THING — BEFORE onCreate()
        val localeCode = Preferences.getStringPreference(newBase, SELECTED_LANGUAGE_CODE) ?: "en"
        super.attachBaseContext(CommonUtils.setLocale(newBase, localeCode))
    }*/
}