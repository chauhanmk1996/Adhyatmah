package com.app.adhyatmah.presentation.ui.pandit_ji.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.adhyatmah.domain.model.pandit_list.get_pandit_list.GetPanditResponse
import com.app.adhyatmah.domain.repository.ApiRepository
import com.app.adhyatmah.utils.common_utils.Resources
import com.app.adhyatmah.utils.common_utils.SingleLiveEvent
import kotlinx.coroutines.launch
import javax.inject.Inject

class PanditListViewModel @Inject constructor(app: Application) : AndroidViewModel(app) {

    private val singleLiveEventPanditList = SingleLiveEvent<Resources<GetPanditResponse>>()

    fun hitPanditListApi(name: String? = null, serviceName: String? = null) {
        singleLiveEventPanditList.postValue(Resources.loading(null))
        viewModelScope.launch {
            try {
                Log.d(
                    "PanditListViewModel",
                    "hitPanditListApi called: name=$name serviceName=$serviceName"
                )
                val response = ApiRepository().getPanditListApi(name, serviceName)
                Log.d(
                    "PanditListViewModel",
                    "API success: vendors=${response.payload.vendors?.size ?: 0}"
                )
                singleLiveEventPanditList.postValue(Resources.success(response))
            } catch (ex: Exception) {
                Log.e(
                    "PanditListViewModel",
                    "API failed -> name=$name serviceName=$serviceName",
                    ex
                )
                singleLiveEventPanditList.postValue(
                    Resources.error(
                        ex.localizedMessage ?: "" ?: "Error", null
                    )
                )
            }
        }
    }

    fun getPanditListLiveData(): LiveData<Resources<GetPanditResponse>> = singleLiveEventPanditList
}