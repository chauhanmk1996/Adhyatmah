package com.app.adhyatmah.presentation.ui.pandit_ji

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.FragmentBookingBinding
import com.app.adhyatmah.presentation.ui.pandit_ji.adapter.HelpCenterAdapter
import com.app.adhyatmah.presentation.ui.pandit_ji.booking.CancelledBookingFragment
import com.app.adhyatmah.presentation.ui.pandit_ji.booking.OngoingBookingFragment
import com.app.adhyatmah.presentation.ui.pandit_ji.booking.PendingBookingFragment
import com.app.adhyatmah.presentation.ui.pandit_ji.booking.PreviousBookingFragment
import com.app.adhyatmah.presentation.ui.pandit_ji.booking.UpcomingBookingsFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class BookingFragment : Fragment() {
    private lateinit var binding: FragmentBookingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadTabs()
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun initViews() {
        /* binding.ivBack.setOnClickListener {
             findNavController().popBackStack()
         }*/
        loadTabs()
    }

    private fun loadTabs() {
        val fragments: List<Fragment> =
            listOf(
                PendingBookingFragment(),
                OngoingBookingFragment(),
                UpcomingBookingsFragment(),
                PreviousBookingFragment(),
                CancelledBookingFragment(),
            )
        val adapter = HelpCenterAdapter(this, fragments)
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 1

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.pendings)
                1 -> getString(R.string.ongoing)
                2 -> getString(R.string.upcoming)
                3 -> getString(R.string.previous)
                else -> getString(R.string.cancelled)
            }
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                refreshFragment(tab.position)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                refreshFragment(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })


    }
    private fun refreshFragment(position: Int) {
        val tag = "f$position"   // ViewPager2 fragment tag
        val fragment = childFragmentManager.findFragmentByTag(tag)

        when (fragment) {
            is PendingBookingFragment -> fragment.refresh()
            is OngoingBookingFragment -> fragment.refresh()
            is UpcomingBookingsFragment -> fragment.refresh()
            is PreviousBookingFragment -> fragment.refresh()
            is CancelledBookingFragment -> fragment.refresh()
        }
    }


}