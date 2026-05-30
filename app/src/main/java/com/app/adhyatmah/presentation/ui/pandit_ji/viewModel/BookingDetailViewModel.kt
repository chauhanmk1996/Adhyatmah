package com.app.adhyatmah.presentation.ui.pandit_ji.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.adhyatmah.domain.model.get_services.GetPujaKitResponse
import com.app.adhyatmah.domain.model.get_services.GetServicesResponse
import com.app.adhyatmah.domain.repository.ApiRepository
import com.app.adhyatmah.utils.common_utils.Resources
import com.app.adhyatmah.utils.common_utils.SingleLiveEvent
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookingDetailViewModel@Inject constructor(application: Application):AndroidViewModel(application) {
    private val getServicesLiveData = SingleLiveEvent<Resources<GetServicesResponse>>()
    private val getPujaKitResponse = SingleLiveEvent<Resources<GetPujaKitResponse>>()

    fun getServices(): LiveData<Resources<GetServicesResponse>> {
        return getServicesLiveData
    }
    fun hitGetServices(panditId: String) {
        try {
            getServicesLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    getServicesLiveData.postValue(
                        Resources.success(
                            ApiRepository().getPanditjiServicesApi(panditId)
                        )
                    )
                } catch (ex: Exception) {
                    getServicesLiveData.postValue(Resources.error(ex.localizedMessage, null))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getAddOnKit(): LiveData<Resources<GetPujaKitResponse>> {
        return getPujaKitResponse
    }

    fun hitGetAddOnKit(serviceId: String) {
        try {
            getPujaKitResponse.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    getPujaKitResponse.postValue(Resources.success(ApiRepository().getAddOnKitApi(serviceId)))
                } catch (ex: Exception) {
                    getPujaKitResponse.postValue(Resources.error(ex.localizedMessage?:"", null))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}