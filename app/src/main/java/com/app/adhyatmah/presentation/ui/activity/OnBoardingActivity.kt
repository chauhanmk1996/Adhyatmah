package com.app.adhyatmah.presentation.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.ActivityOnBoardingBinding
import com.app.adhyatmah.domain.model.auth.GetLandingPageResponse
import com.app.adhyatmah.presentation.ui.adapter.OnboardingAdapter
import com.app.adhyatmah.presentation.ui.viewmodel.AuthViewModel
import com.app.adhyatmah.utils.base.BaseActivity
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import kotlin.getValue

class OnBoardingActivity : BaseActivity<ActivityOnBoardingBinding>() {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var adapter: OnboardingAdapter
    private var currentPage = 0

    override fun getLayoutId(): Int {
        return R.layout.activity_on_boarding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT
        authViewModel.getLandingPagesData()
        setObserver()
    }

    fun setAdapter(landingPage: List<GetLandingPageResponse.LandingPage>) {
        adapter = OnboardingAdapter(landingPage)
        binding.onboardingViewPager.adapter = adapter
        binding.onboardingViewPager.offscreenPageLimit = 1
        binding.onboardingViewPager.clipToPadding = false
        binding.onboardingViewPager.clipChildren = false
        binding.onboardingViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        binding.onboardingViewPager.adapter = adapter
        binding.dotsIndicator.setViewPager2(binding.onboardingViewPager)

        binding.onboardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position

            }
        })

        binding.btnNext.setOnClickListener {
            val totalPages = adapter.itemCount
            if (currentPage < totalPages - 1) {
                binding.onboardingViewPager.currentItem = currentPage + 1
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun setObserver() {
        authViewModel.getLandingPageData().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            setAdapter(it.data.payload.landingPages)
                        }

                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(this, true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
    }
}