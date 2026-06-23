package com.app.adhyatmah.utils.base

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

open class BaseActivity<DB : ViewDataBinding> : AppCompatActivity() {

    private var _binding: DB? = null
    protected val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, getLayoutId())
        binding.lifecycleOwner = this
        setTransparentStatusBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    open fun getLayoutId(): Int {
        return 0
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setTransparentStatusBar() {
        window.apply {
            statusBarColor = Color.TRANSPARENT
            WindowCompat.setDecorFitsSystemWindows(this, false)

            decorView.setOnApplyWindowInsetsListener { view, insets ->
                val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                if (isThreeButtonNavigation()) {
                    view.setPadding(
                        0,
                        0,
                        0,
                        systemBarsInsets.bottom
                    ) // Add padding only for 3-button mode
                } else {
                    view.setPadding(0, 0, 0, 0) // No extra padding in gesture mode
                }

                insets
            }
            updateStatusBarIcons(isColorLight(window.decorView.backgroundColor()))
        }
    }

    private fun isThreeButtonNavigation(): Boolean {
        val resourceId =
            resources.getIdentifier("config_navBarInteractionMode", "integer", "android")
        return if (resourceId > 0) {
            resources.getInteger(resourceId) == 0
        } else {
            false
        }
    }

    fun updateStatusBarIcons(isLightBackground: Boolean) {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = isLightBackground
        }
    }

    fun isColorLight(color: Int): Boolean {
        val darkness = 1 - (0.299 * ((color shr 16) and 0xFF) +
                0.587 * ((color shr 8) and 0xFF) +
                0.114 * (color and 0xFF)) / 255
        return darkness < 0.5
    }

    private fun View.backgroundColor(): Int {
        val background = background
        return if (background is ColorDrawable) {
            background.color
        } else {
            Color.WHITE
        }
    }

    override fun applyOverrideConfiguration(overrideConfig: Configuration?) {
        overrideConfig?.let {
            val uiMode = it.uiMode
            it.setTo(baseContext.resources.configuration)
            it.uiMode = uiMode
        }
        super.applyOverrideConfiguration(overrideConfig)
    }
}