package com.app.adhyatmah.presentation.ui.pandit_ji.booking

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.ON_GOING
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.databinding.FragmentOngoingBookingBinding
import com.app.adhyatmah.presentation.ui.pandit_ji.adapter.PreviousBookingAdapter
import com.app.adhyatmah.presentation.ui.pandit_ji.viewModel.BookingViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.panditji.data.model.get_booking.GetBookingResponse
import com.app.adhyatmah.domain.model.update_booking_status.UpdateBookingStatusRequest

class OngoingBookingFragment : BaseFragment<FragmentOngoingBookingBinding>() {

    private val viewModel: BookingViewModel by viewModels()
    private lateinit var previousBookingAdapter: PreviousBookingAdapter

    override fun setLayout(): Int = R.layout.fragment_ongoing_booking

    override fun initView(savedInstanceState: Bundle?) {
        setupRecyclerView()
        hitOngoingBookingApi()
        observeBookings()
    }

    fun refresh() {
        hitOngoingBookingApi()
    }

    private fun setupRecyclerView() {
        previousBookingAdapter = PreviousBookingAdapter(
            requireActivity(),
            ON_GOING,
            mutableListOf(),
            ::updateBookingStatus,
            {}
        ) {
            Log.d("TAG", "Item clicked: $it")
        }
        binding.rcvUpComing.adapter = previousBookingAdapter
    }

    private fun updateBookingStatus(data: GetBookingResponse.Payload.Booking) {
        Log.i("TAG", "updateBookingStatus: ")
        viewModel.hitUpdateBooking(
            Preferences.getStringPreference(requireContext(), ACCESS_TOKEN) ?: "",
            UpdateBookingStatusRequest(
                bookingId = data._id,
                status = "completed"
            )
        )
    }

    private fun hitOngoingBookingApi() {
        viewModel.hitGetBookings(
            "ongoing"
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
                    Log.e("OngoingBookingFragment", "Error: ${resource.message}")
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireActivity(), true)
                }
            }
        }

        viewModel.getUpdateBooking().observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    hitOngoingBookingApi()
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog(true)
                    Log.e("OngoingBookingFragment", "Error: ${resource.message}")
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
            ON_GOING,
            list.toMutableList(),
            ::updateBookingStatus,
            {}
        ) {
            Log.d("TAG", "loadRcvBooking: $it")
        }
        binding.rcvUpComing.adapter = previousBookingAdapter
    }
}