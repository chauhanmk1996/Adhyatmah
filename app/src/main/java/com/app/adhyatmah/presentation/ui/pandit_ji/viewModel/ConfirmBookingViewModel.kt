package com.app.adhyatmah.presentation.ui.pandit_ji.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.adhyatmah.domain.model.booking_payment.BookingPaymentRequest
import com.app.adhyatmah.domain.model.booking_payment.BookingPaymentResponse
import com.app.adhyatmah.domain.model.create_booking.BookPanditJiRequest
import com.app.adhyatmah.domain.model.create_booking.PanditJiBookingResponse
import com.app.adhyatmah.domain.repository.ApiRepository
import com.app.adhyatmah.utils.common_utils.Resources
import com.app.adhyatmah.utils.common_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmBookingViewModel@Inject constructor(application: Application):AndroidViewModel(application) {

    private val bookPanditJiLiveData = MutableLiveData<Resources<PanditJiBookingResponse>>()

    fun getBookPanditJi(): LiveData<Resources<PanditJiBookingResponse>> {
        return bookPanditJiLiveData
    }

    fun hitBookPanditJi(request : BookPanditJiRequest) {
        try {
            bookPanditJiLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    bookPanditJiLiveData.postValue(
                        Resources.success(
                            ApiRepository().panditJiBookingApi(request)
                        )
                    )
                } catch (ex: Exception) {
                    bookPanditJiLiveData.postValue(Resources.error(ex.localizedMessage?:"", null))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private val bookingPaymentLiveData = SingleLiveEvent<Resources<BookingPaymentResponse>>()
    fun getBookingPaymentLiveData(): LiveData<Resources<BookingPaymentResponse>> {
        return bookingPaymentLiveData
    }
    fun hitBookingPayment(request : BookingPaymentRequest) {
        try {
            bookingPaymentLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    bookingPaymentLiveData.postValue(
                        Resources.success(
                            ApiRepository().createBookingPaymentApi(request)
                        )
                    )
                } catch (ex: Exception) {
                    bookingPaymentLiveData.postValue(Resources.error(ex.localizedMessage?:"", null))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}