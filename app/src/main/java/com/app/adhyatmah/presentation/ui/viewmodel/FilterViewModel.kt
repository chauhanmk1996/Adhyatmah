package com.app.adhyatmah.presentation.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.adhyatmah.domain.model.auth.GetLoginResponse
import com.app.adhyatmah.domain.model.delete_account.delete_request.DeleteRequest
import com.app.adhyatmah.domain.model.delete_account.delete_response.DeleteResponse
import com.app.adhyatmah.domain.model.fetch_wish_data.FetchWishListResponse
import com.app.adhyatmah.domain.model.filter.getFilter.GetFilterResponse
import com.app.adhyatmah.domain.model.logout.LogOutRequest
import com.app.adhyatmah.domain.model.privacy_policy.TermPrivacyResponse
import com.app.adhyatmah.domain.model.view_all_product.response.ViewAllProductResponse
import com.app.adhyatmah.domain.repository.ApiRepository
import com.app.adhyatmah.utils.common_utils.Resources
import com.app.adhyatmah.utils.common_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    private val shortCollectionLiveData = SingleLiveEvent<Resources<ViewAllProductResponse>>()
    private val privacyLiveData = SingleLiveEvent<Resources<TermPrivacyResponse>>()
    private val logOutLiveData = SingleLiveEvent<Resources<GetLoginResponse>>()
    private val deleteAccountLiveData = SingleLiveEvent<Resources<DeleteResponse>>()
    private val filterLiveData = SingleLiveEvent<Resources<ViewAllProductResponse>>()
    var selectedColors = mutableListOf<String>()
    var selectedSizes = mutableListOf<String>()
    var selectedCategories = mutableListOf<String>()
    var selectedBrands = mutableListOf<String>()
    var minPrice: Float = 0f
    var maxPrice: Float = 0f
    private val fetchWishLiveData = SingleLiveEvent<Resources<FetchWishListResponse>>()
    private val getFilterLiveData = SingleLiveEvent<Resources<GetFilterResponse>>()

    fun hitPrivacyApi() {
        try {
            privacyLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    privacyLiveData.postValue(
                        Resources.success(
                            ApiRepository().getPrivacyAPIs()
                        )
                    )
                } catch (ex: Exception) {
                    privacyLiveData.postValue(Resources.error(ex.localizedMessage ?: "", null))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun hitLogOutApi(token: LogOutRequest) {
        try {
            logOutLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    logOutLiveData.postValue(
                        Resources.success(
                            ApiRepository().getLogOutAPIs(token)
                        )
                    )
                } catch (ex: Exception) {
                    logOutLiveData.postValue(Resources.error(ex.localizedMessage ?: "", null))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun hitDeleteApi(token: DeleteRequest) {
        try {
            deleteAccountLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    deleteAccountLiveData.postValue(
                        Resources.success(
                            ApiRepository().deleteAccountAPI(token)
                        )
                    )
                } catch (ex: Exception) {
                    deleteAccountLiveData.postValue(
                        Resources.error(
                            ex.localizedMessage ?: "",
                            null
                        )
                    )
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getWishListsData(token: String) {
        try {
            fetchWishLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    fetchWishLiveData.postValue(
                        Resources.success(
                            ApiRepository().getWishListApi(token)
                        )
                    )
                } catch (ex: Exception) {
                    fetchWishLiveData.postValue(Resources.error(ex.localizedMessage ?: "", null))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun hitGetFilterData() {
        try {
            getFilterLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    getFilterLiveData.postValue(
                        Resources.success(
                            ApiRepository().getFilterAPI()
                        )
                    )
                } catch (ex: Exception) {
                    getFilterLiveData.postValue(Resources.error(ex.localizedMessage ?: "", null))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getWishListData(): LiveData<Resources<FetchWishListResponse>> {
        return fetchWishLiveData
    }

    fun getFilterData(): LiveData<Resources<GetFilterResponse>> {
        return getFilterLiveData
    }

    fun getPrivacyList(): LiveData<Resources<TermPrivacyResponse>> {
        return privacyLiveData
    }

    fun getLogout(): LiveData<Resources<GetLoginResponse>> {
        return logOutLiveData
    }

    fun getDeleteAccount(): LiveData<Resources<DeleteResponse>> {
        return deleteAccountLiveData
    }

    fun getApplyFilter(): LiveData<Resources<ViewAllProductResponse>> {
        return filterLiveData
    }

    fun getShortCollectionList(): LiveData<Resources<ViewAllProductResponse>> {
        return shortCollectionLiveData
    }
}