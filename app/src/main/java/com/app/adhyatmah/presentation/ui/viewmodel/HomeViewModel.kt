package com.app.adhyatmah.presentation.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.adhyatmah.domain.model.wish_list.wish_list_request.AddWishListRequest
import com.app.adhyatmah.domain.model.AllCategoryListResponse
import com.app.adhyatmah.domain.model.HomeResponse
import com.app.adhyatmah.domain.model.PopularPujaResponse
import com.app.adhyatmah.domain.model.ProductReviewListResponse
import com.app.adhyatmah.domain.model.TrendingSectionResponse
import com.app.adhyatmah.domain.model.add_to_bag.add_to_bag_request.AddToBagRequest
import com.app.adhyatmah.domain.model.add_to_bag.add_to_bag_response.AddtoBagResponse
import com.app.adhyatmah.domain.model.currency.get_currency.GetCurrencyResponse
import com.app.adhyatmah.domain.model.currency.post_currency.post_currency_response.PostCurrencyResponse
import com.app.adhyatmah.domain.model.home_banner_response.HomeBannerResponse
import com.app.adhyatmah.domain.model.home_blog_response.HomeBlogResponse
import com.app.adhyatmah.domain.model.home_collection_Response.HomeCollectionResponse
import com.app.adhyatmah.domain.model.home_menu_response.HomeMenuResponse
import com.app.adhyatmah.domain.model.pandit_list.get_pandit_list.GetPanditResponse
import com.app.adhyatmah.domain.model.product_detail_response.ProductDetailResponse
import com.app.adhyatmah.domain.model.profile.manage_address.CustomerAddressResponse
import com.app.adhyatmah.domain.model.profile.manage_address.ManageAddressRequest
import com.app.adhyatmah.domain.model.search_list_response.get_search_types_response.GetSearchTypeResponse
import com.app.adhyatmah.domain.model.search_list_response.search_list_api_response.GetSearchListResponse
import com.app.adhyatmah.domain.model.search_list_response.search_list_request.SearchListRequest
import com.app.adhyatmah.domain.model.view_all_product.request.ViewAllProductRequest
import com.app.adhyatmah.domain.model.view_all_product.response.ViewAllProductResponse
import com.app.adhyatmah.domain.model.wish_list.remove_wish_list.RemoveWishListResponse
import com.app.adhyatmah.domain.model.wish_list.wish_list_response.AddWishListResponse
import com.app.adhyatmah.domain.repository.ApiRepository
import com.app.adhyatmah.utils.common_utils.Resources
import com.app.adhyatmah.utils.common_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    private val homeCollectionLiveData = SingleLiveEvent<Resources<HomeCollectionResponse>>()
    private val homeBannerLiveData = SingleLiveEvent<Resources<HomeBannerResponse>>()
    private val homeBlogLiveData = SingleLiveEvent<Resources<HomeBlogResponse>>()
    private val homeMenuLiveData = SingleLiveEvent<Resources<HomeMenuResponse>>()
    private val homeProductDtLiveData = SingleLiveEvent<Resources<ProductDetailResponse>>()
    private val getViewAllLiveData = SingleLiveEvent<Resources<ViewAllProductResponse>>()
    private val getCurrencyLiveData = SingleLiveEvent<Resources<GetCurrencyResponse>>()
    private val postCurrencyLiveData = SingleLiveEvent<Resources<PostCurrencyResponse>>()
    private val addToaBagLiveData = SingleLiveEvent<Resources<AddtoBagResponse>>()
    private val getCateAllLiveData = SingleLiveEvent<Resources<AllCategoryListResponse>>()
    private val getAllProductReviews = SingleLiveEvent<Resources<ProductReviewListResponse>>()
    private val addWishListData = SingleLiveEvent<Resources<AddWishListResponse>>()
    private val removeWishListLiveData = SingleLiveEvent<Resources<RemoveWishListResponse>>()
    private val singleLiveEventPanditList = SingleLiveEvent<Resources<GetPanditResponse>>()
    private val trendingSectionResponse = SingleLiveEvent<Resources<TrendingSectionResponse>>()
    private val getAddressLiveData = SingleLiveEvent<Resources<CustomerAddressResponse>>()
    private val homeResponse = SingleLiveEvent<Resources<HomeResponse>>()
    private val popularPujaResponse = SingleLiveEvent<Resources<PopularPujaResponse>>()

    fun trendingSectionApi() {
        trendingSectionResponse.postValue(Resources.loading(null))
        viewModelScope.launch {
            try {
                val response = ApiRepository().trendingSectionApi()
                trendingSectionResponse.postValue(Resources.success(response))
            } catch (ex: Exception) {
                trendingSectionResponse.postValue(Resources.error(ex.localizedMessage ?: "", null))
            }
        }
    }

    fun getTrendingSectionLiveData(): LiveData<Resources<TrendingSectionResponse>> =
        trendingSectionResponse

    fun hitPanditListApi(name: String? = null, serviceName: String? = null,page:Int) {
        singleLiveEventPanditList.postValue(Resources.loading(null))
        viewModelScope.launch {
            try {
                val response = ApiRepository().getPanditListApi(name, serviceName,page)
                singleLiveEventPanditList.postValue(Resources.success(response))
            } catch (ex: Exception) {
                singleLiveEventPanditList.postValue(
                    Resources.error(
                        ex.localizedMessage ?: "",
                        null
                    )
                )
            }
        }
    }

    fun getPanditListLiveData(): LiveData<Resources<GetPanditResponse>> = singleLiveEventPanditList

    fun homeDataApi() {
        homeResponse.postValue(Resources.loading(null))
        viewModelScope.launch {
            try {
                val response = ApiRepository().homeDataApi()
                homeResponse.postValue(Resources.success(response))
            } catch (ex: Exception) {
                homeResponse.postValue(Resources.error(ex.localizedMessage ?: "", null))
            }
        }
    }

    fun getHomeDataApi(): LiveData<Resources<HomeResponse>> = homeResponse

    fun addToBagApi(request: AddToBagRequest) {
        try {
            addToaBagLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    addToaBagLiveData.postValue(
                        Resources.success(
                            ApiRepository().addToBagApi(request)
                        )
                    )
                } catch (ex: Exception) {
                    addToaBagLiveData.postValue(Resources.error(ex.localizedMessage ?: "", null))
                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun homeCollectionApi(token: String) {
        try {
            homeCollectionLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    val response = ApiRepository().getHomeCollectionApiApi(token)
                    homeCollectionLiveData.postValue(Resources.success(response))
                } catch (ex: Exception) {
                    homeCollectionLiveData.postValue(
                        Resources.error(
                            ex.localizedMessage ?: "",
                            null
                        )
                    )
                }
            }

            homeBannerLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    homeBannerLiveData.postValue(
                        Resources.success(
                            ApiRepository().getHomeBannerApi()
                        )
                    )
                } catch (ex: Exception) {
                    homeBannerLiveData.postValue(
                        Resources.error(
                            ex.localizedMessage ?: "",
                            null
                        )
                    )
                }
            }

            homeBlogLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    homeBlogLiveData.postValue(
                        Resources.success(
                            ApiRepository().getHomeBlogApi()
                        )
                    )
                } catch (ex: Exception) {
                    homeBlogLiveData.postValue(
                        Resources.error(
                            ex.localizedMessage ?: "",
                            null
                        )
                    )
                }
            }

            homeMenuLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    homeMenuLiveData.postValue(
                        Resources.success(
                            ApiRepository().getHomeMenuApi()
                        )
                    )
                } catch (ex: Exception) {
                    homeMenuLiveData.postValue(
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

    fun getCollectionLiveData(): LiveData<Resources<HomeCollectionResponse>> {
        return homeCollectionLiveData
    }

    fun getViewAllData(token: String, handle: ViewAllProductRequest) {
        try {
            getViewAllLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    getViewAllLiveData.postValue(
                        Resources.success(
                            ApiRepository().getViewAllDataApi(token, handle)
                        )
                    )
                } catch (ex: Exception) {
                    getViewAllLiveData.postValue(Resources.error(ex.localizedMessage ?: "", null))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun addWishLisData(request: AddWishListRequest) {
        try {
            addWishListData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    addWishListData.postValue(
                        Resources.success(
                            ApiRepository().addWishListApi(
                                request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    addWishListData.postValue(Resources.error(ex.localizedMessage ?: "", null))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun removeWishLisData(request: AddWishListRequest) {
        try {
            removeWishListLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    removeWishListLiveData.postValue(
                        Resources.success(
                            ApiRepository().removeWishList(
                                request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    removeWishListLiveData.postValue(
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

    fun getProductDtData(id: String, token: String) {
        homeProductDtLiveData.postValue(Resources.loading(null))
        viewModelScope.launch {
            try {
                val res = ApiRepository().getProductDetailsApi(id, token)
                homeProductDtLiveData.postValue(
                    Resources.success(res)
                )
            } catch (ex: Exception) {
                homeProductDtLiveData.postValue(Resources.error(ex.localizedMessage ?: "", null))
            }
        }

    }

    fun getAllCtData(page: Int, limit: Int) {
        getCateAllLiveData.postValue(Resources.loading(null))
        viewModelScope.launch {
            try {
                val res = ApiRepository().getAllCatDetailsApi(page, limit)
                getCateAllLiveData.postValue(
                    Resources.success(res)
                )
            } catch (ex: Exception) {
                getCateAllLiveData.postValue(Resources.error(ex.localizedMessage ?: "", null))
            }
        }
    }


    fun getAllProductReview(productId: String, page: Int, limit: Int) {
        getAllProductReviews.postValue(Resources.loading(null))
        viewModelScope.launch {
            try {
                val res = ApiRepository().getAllReviewList(productId, page, limit)
                getAllProductReviews.postValue(
                    Resources.success(res)
                )
            } catch (ex: Exception) {
                getAllProductReviews.postValue(Resources.error(ex.localizedMessage ?: "", null))
            }
        }

    }


    private val searchListLiveData = SingleLiveEvent<Resources<GetSearchListResponse>>()

    fun getSearchListData(request: SearchListRequest) {
        try {
            searchListLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    searchListLiveData.postValue(
                        Resources.success(
                            ApiRepository().searchListApi(request)
                        )
                    )
                } catch (ex: Exception) {
                    searchListLiveData.postValue(Resources.error(ex.localizedMessage ?: "", null))
                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getSearchListRes(): LiveData<Resources<GetSearchListResponse>> {
        return searchListLiveData
    }

    private val searchTypeListLiveData = SingleLiveEvent<Resources<GetSearchTypeResponse>>()

    fun getSearchTypeListData() {
        try {
            searchTypeListLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    searchTypeListLiveData.postValue(
                        Resources.success(
                            ApiRepository().searchTypeListApi()
                        )
                    )
                } catch (ex: Exception) {
                    searchTypeListLiveData.postValue(
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

    fun getSearchTypeListResponse(): LiveData<Resources<GetSearchTypeResponse>> {
        return searchTypeListLiveData
    }

    fun getBannerLiveData(): LiveData<Resources<HomeBannerResponse>> {
        return homeBannerLiveData
    }

    fun getProductLiveData(): LiveData<Resources<ProductDetailResponse>> {
        return homeProductDtLiveData
    }

    fun getViewAllLiveData(): LiveData<Resources<ViewAllProductResponse>> {
        return getViewAllLiveData
    }

    fun getPostCurrencyLiveData(): LiveData<Resources<PostCurrencyResponse>> {
        return postCurrencyLiveData
    }

    fun getCurrencyLiveData(): LiveData<Resources<GetCurrencyResponse>> {
        return getCurrencyLiveData
    }

    fun getAllCatLiveData(): LiveData<Resources<AllCategoryListResponse>> {
        return getCateAllLiveData
    }

    fun getAllPdReviewLiveData(): LiveData<Resources<ProductReviewListResponse>> {
        return getAllProductReviews
    }

    fun getAddWishList(): LiveData<Resources<AddWishListResponse>> {
        return addWishListData
    }

    fun removeWishList(): LiveData<Resources<RemoveWishListResponse>> {
        return removeWishListLiveData
    }

    fun getAddToBag(): LiveData<Resources<AddtoBagResponse>> {
        return addToaBagLiveData
    }

    fun getCustomerAddressRes(): LiveData<Resources<CustomerAddressResponse>> {
        return getAddressLiveData
    }

    fun getAddressData(token: ManageAddressRequest) {
        try {
            getAddressLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    getAddressLiveData.postValue(
                        Resources.success(
                            ApiRepository().getCustomerAddApi(token)
                        )
                    )
                } catch (ex: Exception) {
                    getAddressLiveData.postValue(Resources.error(ex.localizedMessage ?: "", null))
                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun popularPujaListApi() {
        popularPujaResponse.postValue(Resources.loading(null))
        viewModelScope.launch {
            try {
                val response = ApiRepository().getAllPopularPujaListApi()
                popularPujaResponse.postValue(Resources.success(response))
            } catch (ex: Exception) {
                popularPujaResponse.postValue(Resources.error(ex.localizedMessage ?: "", null))
            }
        }
    }

    fun getPopularPujaListLiveData(): LiveData<Resources<PopularPujaResponse>> = popularPujaResponse
}