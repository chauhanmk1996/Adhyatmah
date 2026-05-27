package com.app.adhyatmah.presentation.ui.pandit_ji.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.adhyatmah.domain.create_order.creat_order_request.CreaterOrderRequest
import com.app.adhyatmah.domain.create_order.creater_order_response.CreateCODResponse
import com.app.adhyatmah.domain.model.booking_payment.BookingPaymentRequest
import com.app.adhyatmah.domain.model.booking_payment.BookingPaymentResponse
import com.app.adhyatmah.domain.model.create_booking.PanditjiBookingRequest
import com.app.adhyatmah.domain.model.create_booking.PanditjiBookingResponse
import com.app.adhyatmah.domain.repository.ApiRepository
import com.app.adhyatmah.presentation.ui.adapter.ReviewsAdapter
import com.app.adhyatmah.utils.common_utils.Resources
import com.app.adhyatmah.utils.common_utils.SingleLiveEvent
import com.app.panditji.data.model.get_booking.GetBookingResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmBookingViewModel@Inject constructor(application: Application):AndroidViewModel(application) {
    private val bookPanditjiLiveData = SingleLiveEvent<Resources<PanditjiBookingResponse>>()
    fun getBookPanditji(): LiveData<Resources<PanditjiBookingResponse>> {
        return bookPanditjiLiveData
    }
    fun hitBookPanditji(request : PanditjiBookingRequest) {
        try {
            bookPanditjiLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {

                    bookPanditjiLiveData.postValue(
                        Resources.success(
                            ApiRepository().panditjiBookingAPI(request)
                        )

                    )
                } catch (ex: Exception) {
                    bookPanditjiLiveData.postValue(Resources.error(ex.localizedMessage, null))

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
                    bookingPaymentLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}