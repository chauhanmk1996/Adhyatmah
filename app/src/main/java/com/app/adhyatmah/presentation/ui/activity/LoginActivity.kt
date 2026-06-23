package com.app.adhyatmah.presentation.ui.activity

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.ActivityLoginBinding
import com.app.adhyatmah.utils.base.BaseActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val screen = intent.getStringExtra("screen")

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
}