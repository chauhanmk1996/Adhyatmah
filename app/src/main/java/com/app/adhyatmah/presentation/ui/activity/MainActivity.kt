package com.app.adhyatmah.presentation.ui.activity

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.ActivityMainBinding
import com.app.adhyatmah.utils.base.BaseActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : BaseActivity<ActivityMainBinding>() {

    var panditJiFromPopularPuja: Boolean = false

    fun setBottomNavSelected(itemId: Int) {
        binding.bottomNavigationView.selectedItemId = itemId
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bottomNav()
    }

    fun updateBagBadge(count: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val badge = bottomNav.getOrCreateBadge(R.id.navigation_bag)

        if (count > 0) {
            badge.isVisible = true
            badge.number = count
        } else {
            badge.isVisible = false
        }
    }

    fun bottomNav() {
        val navController = findNavController(R.id.nav_host_fragment)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNav.setupWithNavController(navController)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.navigation_pandit_ji -> {
                    navController.navigate(R.id.panditJiFragment)
                    true
                }

                R.id.navigation_category -> {
                    navController.navigate(R.id.categoryProductFragment)
                    true
                }

                R.id.navigation_bag -> {
                    //    navController.navigate(R.id.razorpayFragment)
                    navController.navigate(R.id.bagFragment)
                    true
                }

                R.id.navigation_profile -> {
                    navController.navigate(R.id.profileFragment)
                    // navController.navigate(R.id.profileFragment)
                    true
                }

                else -> false
            }
        }


        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    setBottomBarPadding()
                    showBottomNav()
                }

                R.id.categoryFragment -> {
                    setBottomBarPadding()
                    showBottomNav()
                }

                R.id.bagFragment -> {
                    setBottomBarPadding()
                    showBottomNav()
                }

                R.id.profileFragment -> {
                    setBottomBarPadding()
                    showBottomNav()
                }

                R.id.wishListFragment -> {
                    hideBottomNav()
                    clearBottomBarPadding()
                }

                R.id.myOrderFragment -> {
                    hideBottomNav()
                    clearBottomBarPadding()
                }

                R.id.mangeAddressFragment -> {
                    hideBottomNav()
                    clearBottomBarPadding()
                }

                R.id.termConditionFragment2 -> {
                    bottomNav.visibility = View.GONE
                    clearBottomBarPadding()
                }

                R.id.helpSupportFragment -> {
                    hideBottomNav()
                    clearBottomBarPadding()
                }

                R.id.contactUsFragment -> {
                    hideBottomNav()
                    clearBottomBarPadding()
                }

                R.id.filterFragment -> {
                    hideBottomNav()
                    clearBottomBarPadding()
                }

                R.id.addAddressFragment -> {
                    hideBottomNav()
                    clearBottomBarPadding()
                }

                R.id.brandFragment -> {
                    hideBottomNav()
                    clearBottomBarPadding()
                }

                R.id.reviewsFragment -> {
                    hideBottomNav()
                    clearBottomBarPadding()
                }

                R.id.editProfileFragment -> {
                    clearBottomBarPadding()
                    hideBottomNav()
                }

                R.id.paymentMethodFragment -> {
                    hideBottomNav()
                    clearBottomBarPadding()
                }

                R.id.congratulationFragment -> {
                    hideBottomNav()
                    clearBottomBarPadding()
                }

                R.id.confirmOrderFragment -> {
                    hideBottomNav()
                    clearBottomBarPadding()
                }

                R.id.productDetailsFragment -> {
                    bottomNav.visibility = View.GONE
                    clearBottomBarPadding()
                }

                R.id.productListFragment -> {
                    bottomNav.visibility = View.GONE
                    clearBottomBarPadding()
                }

                R.id.myOrderDetailsFragment -> {
                    bottomNav.visibility = View.GONE
                    clearBottomBarPadding()
                }

                R.id.trackOrderFragment -> {
                    bottomNav.visibility = View.GONE
                    clearBottomBarPadding()
                }

                R.id.sizeImageFragment -> {
                    bottomNav.visibility = View.GONE
                    clearBottomBarPadding()
                }

                R.id.blogDetailsFragment -> {
                    bottomNav.visibility = View.GONE
                    clearBottomBarPadding()
                }

                R.id.paymentGatewayFragment -> {
                    bottomNav.visibility = View.GONE
                    clearBottomBarPadding()
                }

                R.id.searchListFragment -> {
                    bottomNav.visibility = View.GONE
                    clearBottomBarPadding()
                }

                R.id.bookingFragment -> {
                    bottomNav.visibility = View.GONE
                    clearBottomBarPadding()
                }

                R.id.bookingDetailsFragment -> {
                    bottomNav.visibility = View.GONE
                    clearBottomBarPadding()
                }

                R.id.chooseServiceFragment -> {
                    bottomNav.visibility = View.GONE
                    clearBottomBarPadding()
                }

                R.id.chooseAddOnFragment -> {
                    bottomNav.visibility = View.GONE
                    clearBottomBarPadding()
                }

                R.id.dateTimeSelectionFragment -> {
                    bottomNav.visibility = View.GONE
                    clearBottomBarPadding()
                }

                R.id.selectLanguageFragment -> {
                    bottomNav.visibility = View.GONE
                    clearBottomBarPadding()
                }

                R.id.confirmBookingFragment -> {
                    bottomNav.visibility = View.GONE
                    clearBottomBarPadding()
                }

                R.id.chooseLanguageFragment -> {
                    bottomNav.visibility = View.GONE
                    clearBottomBarPadding()
                }

                else -> {
                    bottomNav.visibility = View.VISIBLE
                    setBottomBarPadding()

                }
            }
        }

    }

    private fun clearBottomBarPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root, null)
        binding.bottomNavigationView.setPadding(0, 0, 0, 0)
        binding.bottomNavigationView.setPadding(0, 0, 0, 0)
    }

    fun setBottomBarPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.bottomNavigationView.setPadding(0, 0, 0, 0)
            val offset = if (isGestureNavigation()) systemBars.bottom else 0
            binding.bottomNavigationView.setPadding(0, 0, 0, offset)
            WindowInsetsCompat.CONSUMED
        }
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment)
        val currentDestinationId = navController.currentDestination?.id

        if (currentDestinationId == R.id.panditJiFragment && panditJiFromPopularPuja) {
            panditJiFromPopularPuja = false
            binding.bottomNavigationView.selectedItemId = R.id.navigation_home
            findNavController(R.id.nav_host_fragment).navigate(R.id.popularPujaFragment)
            return
        }

        val baseFragments = setOf(
            R.id.homeFragment,
            R.id.panditJiFragment,
            R.id.categoryFragment,
            R.id.bagFragment,
            R.id.profileFragment
        )

        when {
            currentDestinationId !in baseFragments -> {
                super.onBackPressed()
            }

            currentDestinationId != R.id.homeFragment -> {
                val navOptions = NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setPopUpTo(navController.graph.startDestinationId, inclusive = false)
                    .build()
                navController.navigate(R.id.homeFragment, null, navOptions)
                binding.bottomNavigationView.selectedItemId = R.id.navigation_home
            }

            else -> {
                finish()
            }
        }
    }

    fun showBottomNav() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    fun hideBottomNav() {
        binding.bottomNavigationView.visibility = View.GONE
    }

    private fun isGestureNavigation(): Boolean {
        val resId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android")
        return resId > 0 && resources.getInteger(resId) == 2
    }

    fun switchToPanditJiTab() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.navigation_pandit_ji
    }

    fun switchToCategoryTab() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.navigation_category
    }

    fun switchToCartTab() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.navigation_bag
    }
}