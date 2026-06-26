package com.app.adhyatmah.presentation.ui.pandit_ji.booking

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.PENDING
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.data.preferences.UP_COMING
import com.app.adhyatmah.databinding.FragmentPendingBinding
import com.app.adhyatmah.domain.model.update_booking_status.UpdateBookingStatusRequest
import com.app.adhyatmah.presentation.ui.pandit_ji.adapter.PreviousBookingAdapter
import com.app.adhyatmah.presentation.ui.pandit_ji.viewModel.BookingViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.panditji.data.model.get_booking.GetBookingResponse

class PendingBookingFragment : BaseFragment<FragmentPendingBinding>() {

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
            PENDING,
            mutableListOf(),
            completeBooking = {
            },
            cancelBooking = {},
            callBack = {},
            callClick = { phone ->
                openDialPad(phone)
            }
        )
        binding.rvPending.adapter = adapter
    }

    private fun hitPendingBookingApi() {
        viewModel.hitGetBookings("pending")
    }

    private fun observeBookings() {
        viewModel.getBookings().observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    val data = resource.data?.payload?.bookings

                    if (!data.isNullOrEmpty()) {
                        loadRcvBooking(data)
                        binding.rvPending.visibility = View.VISIBLE
                        binding.tvNoSlots.visibility = View.GONE
                    } else {
                        binding.tvNoSlots.visibility = View.VISIBLE
                        binding.rvPending.visibility = View.GONE
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

        viewModel.getUpdateBooking().observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    hitPendingBookingApi()
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
            UP_COMING,
            list.toMutableList(),
            completeBooking = {
            },
            cancelBooking = {
                showCancelBookingPrompt(it)
            },
            callBack = {},
            callClick = { phone ->
                openDialPad(phone)
            }
        )
        binding.rvPending.adapter = previousBookingAdapter
    }

    private fun showCancelBookingPrompt(data: GetBookingResponse.Payload.Booking) {
        var dialog: AlertDialog? = null
        dialog = CommonUtils.showCustomAlertDialog(
            requireActivity(),
            getString(R.string.alert),
            getString(R.string.there_will_be_no_refund_if_cancelled_before_24_hours_of_the_scheduled_time_do_you_want_to_proceed),
            positiveButtonText =getString(R.string.yes),
            negativeButtonText = getString(R.string.no),
            positiveButtonAction = {
                dialog?.dismiss()
                updateBookingStatus(data)
            },
            negativeButtonAction = {
                dialog?.dismiss()
            }
        )
    }

    private fun updateBookingStatus(data: GetBookingResponse.Payload.Booking) {
        Log.i("TAG", "updateBookingStatus: ")
        viewModel.hitUpdateBooking(
            Preferences.getStringPreference(requireContext(), ACCESS_TOKEN) ?: "",
            UpdateBookingStatusRequest(
                bookingId = data._id,
                status = "cancelled"
            )
        )
    }
}