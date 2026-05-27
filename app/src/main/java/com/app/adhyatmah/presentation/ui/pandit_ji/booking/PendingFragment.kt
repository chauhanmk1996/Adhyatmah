package com.app.adhyatmah.presentation.ui.pandit_ji.booking

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.adhyatmah.R
import com.app.adhyatmah.data.local.IntroSlideData
import com.app.adhyatmah.data.preferences.ON_GOING
import com.app.adhyatmah.data.preferences.PENDING
import com.app.adhyatmah.databinding.FragmentPendingBinding
import com.app.adhyatmah.presentation.ui.pandit_ji.adapter.PreviousBookingAdapter
import com.app.adhyatmah.utils.base.BaseFragment

class PendingFragment : BaseFragment<FragmentPendingBinding>() {
    lateinit var previousBookingAdapter : PreviousBookingAdapter
    override fun setLayout(): Int {
        return R.layout.fragment_pending
    }

    override fun initView(savedInstanceState: Bundle?) {
//        val setList = mutableListOf<IntroSlideData>()
//        setList.clear()
//        setList.addAll(listOf(IntroSlideData("Manish ice cream wala","Noida Sector 64",R.drawable.profile_image)))
//        previousBookingAdapter = PreviousBookingAdapter(
//            requireActivity(),
//            PENDING,
//            setList
//        ) {
//            // requireParentFragment().findNavController().navigate(R.id.action_bookingFragment_to_upcomingBookingDetailsFragment)
//
//            Log.d("TAG", "loadRcvBooking: $it")
//
//        }
//        binding.rcvUpComing.adapter = previousBookingAdapter
    }

}