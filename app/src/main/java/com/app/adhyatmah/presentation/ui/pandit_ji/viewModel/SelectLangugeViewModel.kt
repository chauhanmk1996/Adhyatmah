package com.app.adhyatmah.presentation.ui.pandit_ji.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.adhyatmah.domain.model.GetLanguagesResponse
import com.app.adhyatmah.domain.model.booking_payment.BookingPaymentRequest
import com.app.adhyatmah.domain.model.create_booking.PanditjiBookingRequest
import com.app.adhyatmah.domain.model.create_booking.PanditjiBookingResponse
import com.app.adhyatmah.domain.repository.ApiRepository
import com.app.adhyatmah.utils.common_utils.Resources
import com.app.adhyatmah.utils.common_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectLanguageViewModel@Inject constructor(application: Application):AndroidViewModel(application) {
    private val languagesListLiveData = SingleLiveEvent<Resources<GetLanguagesResponse>>()
    fun getLanguagesLiveData(): LiveData<Resources<GetLanguagesResponse>> {
        return languagesListLiveData
    }
    fun hitGetLanguagesApi() {
        try {
            languagesListLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {

                    languagesListLiveData.postValue(
                        Resources.success(
                            ApiRepository().getAllLanguagesApi()
                        )

                    )
                } catch (ex: Exception) {
                    languagesListLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}