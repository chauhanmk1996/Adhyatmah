package com.app.adhyatmah.presentation.ui.pandit_ji.viewModel

import android.app.Application
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

    fun hitPanditListApi(name: String? = null, serviceName: String? = null,page:Int) {
        singleLiveEventPanditList.postValue(Resources.loading(null))
        viewModelScope.launch {
            try {
                val response = ApiRepository().getPanditListApi(name, serviceName,page)
                singleLiveEventPanditList.postValue(Resources.success(response))
            } catch (ex: Exception) {
                singleLiveEventPanditList.postValue(Resources.error(ex.localizedMessage ?: "", null))
            }
        }
    }

    fun getPanditListLiveData(): LiveData<Resources<GetPanditResponse>> = singleLiveEventPanditList
}