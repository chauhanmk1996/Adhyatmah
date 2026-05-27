package com.app.adhyatmah.presentation.ui.pandit_ji.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.adhyatmah.domain.repository.ApiRepository
import com.app.adhyatmah.utils.common_utils.Resources
import com.app.adhyatmah.utils.common_utils.SingleLiveEvent
import com.app.panditji.data.model.get_booking.GetBookingResponse
import com.app.adhyatmah.domain.model.update_booking_status.UpdateBookingStatusRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// BookingViewModel.kt
@HiltViewModel
class BookingViewModel @Inject constructor(
    application: Application,
) : AndroidViewModel(application) {

    private val bookingsLiveData = SingleLiveEvent<Resources<GetBookingResponse>>()

    fun getBookings(): LiveData<Resources<GetBookingResponse>> = bookingsLiveData

    fun hitGetBookings(status: String, token: String) {
        bookingsLiveData.postValue(Resources.loading(null))
        viewModelScope.launch {
            try {
                val response = ApiRepository().getBookingHistoryApi( status)
                bookingsLiveData.postValue(Resources.success(response))
            } catch (e: Exception) {
                bookingsLiveData.postValue(Resources.error(e.localizedMessage ?: "Error", null))
            }
        }
    }







    private val updateBookingLiveData = SingleLiveEvent<Resources<GetBookingResponse>>()

    fun getUpdateBooking(): LiveData<Resources<GetBookingResponse>> = updateBookingLiveData

    fun hitUpdateBooking(token: String, request: UpdateBookingStatusRequest) {
        updateBookingLiveData.postValue(Resources.loading(null))

        viewModelScope.launch {
            try {
                Log.d("UpdateBooking", "API call started with request: $request")

                val response = ApiRepository().updateBookingStatusApi(token, request)

                Log.d("UpdateBooking", "API call success: $response")

                updateBookingLiveData.postValue(Resources.success(response))
            } catch (e: Exception) {
                Log.e("UpdateBooking", "API call failed", e) // ✅ prints stack trace in Logcat
                updateBookingLiveData.postValue(Resources.error(e.localizedMessage ?: "Error", null))
            }
        }
    }

}
