package com.app.adhyatmah.presentation.ui.pandit_ji.booking

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.PREVIOUS
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.databinding.FragmentPreviousBookingBinding
import com.app.adhyatmah.presentation.ui.pandit_ji.adapter.PreviousBookingAdapter
import com.app.adhyatmah.presentation.ui.pandit_ji.viewModel.BookingViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.panditji.data.model.get_booking.GetBookingResponse

class PreviousBookingFragment : BaseFragment<FragmentPreviousBookingBinding>() {

    private val viewModel: BookingViewModel by viewModels()
    private lateinit var previousBookingAdapter: PreviousBookingAdapter

    override fun setLayout(): Int = R.layout.fragment_previous_booking

    override fun initView(savedInstanceState: Bundle?) {
        setupRecyclerView()
        hitPreviousBookingApi()
        observeBookings()
    }
    fun refresh() {
        hitPreviousBookingApi()
    }

    private fun setupRecyclerView() {
        previousBookingAdapter = PreviousBookingAdapter(
            requireActivity(),
            PREVIOUS,
            mutableListOf(),
            {},
            {}
        ) {
            // Handle item click
            Log.d("TAG", "Item clicked: $it")
            // requireParentFragment().findNavController()
            //     .navigate(R.id.action_bookingFragment_to_upcomingBookingDetailsFragment)
        }
        binding.rcvUpComing.adapter = previousBookingAdapter
    }

    private fun hitPreviousBookingApi() {
        viewModel.hitGetBookings(
            "previous", // or "previous" if your backend uses that status
            Preferences.getStringPreference(requireContext(), ACCESS_TOKEN) ?: ""
        )
    }

    private fun observeBookings() {
        viewModel.getBookings().observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    val data = resource.data?.payload?.bookings

                    if (!data.isNullOrEmpty()) {
                        loadRcvBooking(data)
                        binding.rcvUpComing.visibility = View.VISIBLE
                        binding.tvNoSlots.visibility = View.GONE
                    } else {
                        binding.tvNoSlots.visibility = View.VISIBLE
                        binding.rcvUpComing.visibility = View.GONE
                    }
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog(true)
                    Log.e("PreviousBookingFragment", "Error: ${resource.message}")
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireActivity(), true)
                }
            }
        }
    }

    private fun loadRcvBooking(list: List<GetBookingResponse.Payload.Booking>) {
        previousBookingAdapter = PreviousBookingAdapter(
            requireActivity(),
            PREVIOUS,
            list.toMutableList(),
            {},
            {}
        ) {
            Log.d("TAG", "loadRcvBooking: $it")
        }
        binding.rcvUpComing.adapter = previousBookingAdapter
    }
}
