package com.app.adhyatmah.presentation.ui.pandit_ji.booking

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.CANCELLED
import com.app.adhyatmah.databinding.FragmentPendingBinding
import com.app.adhyatmah.presentation.ui.pandit_ji.adapter.PreviousBookingAdapter
import com.app.adhyatmah.presentation.ui.pandit_ji.viewModel.BookingViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.panditji.data.model.get_booking.GetBookingResponse
import kotlin.getValue

class CancelledBookingFragment : BaseFragment<FragmentPendingBinding>() {

    private val viewModel: BookingViewModel by viewModels()
    private lateinit var adapter: PreviousBookingAdapter

    private lateinit var previousBookingAdapter: PreviousBookingAdapter

    override fun setLayout(): Int = R.layout.fragment_pending

    override fun initView(savedInstanceState: Bundle?) {
        setupRecyclerView()
        hitPendingBookingApi()
        observeBookings()
    }

    fun refresh() {
        hitPendingBookingApi()
    }

    private fun setupRecyclerView() {
        adapter = PreviousBookingAdapter(
            requireActivity(),
            CANCELLED,
            mutableListOf(),
            completeBooking = {},
            cancelBooking = {},
            callBack = {},
            callClick = { phone ->
                openDialPad(phone)
            }
        )
        binding.rcvUpComing.adapter = adapter
    }

    private fun hitPendingBookingApi() {
        viewModel.hitGetBookings(
            "cancelled"
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
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireActivity(), true)
                }
            }
        }
    }

    private fun loadRcvBooking(list: List<GetBookingResponse.Payload.Booking>) {
        adapter = PreviousBookingAdapter(
            requireActivity(),
            CANCELLED,
            list.toMutableList(),
            completeBooking = {},
            cancelBooking = {},
            callBack = {},
            callClick = { phone ->
                openDialPad(phone)
            }
        )
        binding.rcvUpComing.adapter = previousBookingAdapter
    }
}