package com.app.adhyatmah.presentation.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.adhyatmah.domain.model.bag_response.apply_coupons.apply_coupons.ApplyCouponsResponse
import com.app.adhyatmah.domain.model.bag_response.apply_coupons.apply_coupons_req.ApplyCouponsRequest
import com.app.adhyatmah.domain.model.bag_response.apply_coupons.get_all_apply_coupons.GetAllCouponsResponse
import com.app.adhyatmah.domain.model.bag_response.apply_coupons.remove_coupons.RemoveCouponsResponse
import com.app.adhyatmah.domain.model.bag_response.get_cart_list_data.GetCartListResponse
import com.app.adhyatmah.domain.model.bag_response.increase_qty_request.IncreaseQtyRequest
import com.app.adhyatmah.domain.model.bag_response.increase_quantity.CartQtyIncreaseResponse
import com.app.adhyatmah.domain.model.bag_response.remove_coupon.remove_coupon_request.RemoveCouponRequest
import com.app.adhyatmah.domain.model.bag_response.remove_coupon.remove_coupon_response.RemoveCouponResponse
import com.app.adhyatmah.domain.repository.ApiRepository
import com.app.adhyatmah.utils.common_utils.Resources
import com.app.adhyatmah.utils.common_utils.SingleLiveEvent
import kotlinx.coroutines.launch
import javax.inject.Inject

class BagViewModel @Inject constructor(application: Application): AndroidViewModel(application) {

    private val cartListLiveData = SingleLiveEvent<Resources<GetCartListResponse>>()
    private val removeCouponListLiveData = SingleLiveEvent<Resources<RemoveCouponResponse>>()

    fun getCartList(token: String) {
        try {
            cartListLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {

                    cartListLiveData.postValue(
                        Resources.success(
                            ApiRepository().getCartListApi(token)
                        )
                    )
                } catch (ex: Exception) {
                    cartListLiveData.postValue(Resources.error(ex.localizedMessage?:"", null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getCartListData(): LiveData<Resources<GetCartListResponse>> {
        return cartListLiveData
    }


    private val couponsListLiveData = SingleLiveEvent<Resources<GetAllCouponsResponse>>()

    fun getCouponsList() {
        try {
            couponsListLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {

                    couponsListLiveData.postValue(
                        Resources.success(
                            ApiRepository().getCouponListApi()
                        )
                    )
                } catch (ex: Exception) {
                    couponsListLiveData.postValue(Resources.error(ex.localizedMessage?:"", null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
    fun hitRemoveCouponAPI(request:RemoveCouponRequest) {
        try {
            removeCouponListLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {

                    removeCouponListLiveData.postValue(
                        Resources.success(
                            ApiRepository().removeCouponAPI(request)
                        )
                    )
                } catch (ex: Exception) {
                    removeCouponListLiveData.postValue(Resources.error(ex.localizedMessage?:"", null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getCouponsListData(): LiveData<Resources<GetAllCouponsResponse>> {
        return couponsListLiveData
    }
    fun getRemoveCouponData(): LiveData<Resources<RemoveCouponResponse>> {
        return removeCouponListLiveData
    }


    private val increaseQtyLiveData = SingleLiveEvent<Resources<CartQtyIncreaseResponse>>()

    fun getPlusQtyList(request: IncreaseQtyRequest) {
        try {
            increaseQtyLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {

                    increaseQtyLiveData.postValue(
                        Resources.success(
                            ApiRepository().getIncreaseQtyApi(request)
                        )
                    )
                } catch (ex: Exception) {
                    increaseQtyLiveData.postValue(Resources.error(ex.localizedMessage?:"", null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getPlusQtyData(): LiveData<Resources<CartQtyIncreaseResponse>> {
        return increaseQtyLiveData
    }


    private val applyCouponsLiveData = SingleLiveEvent<Resources<ApplyCouponsResponse>>()

    fun applyCouponsData(token : ApplyCouponsRequest) {
        try {
            applyCouponsLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {

                    applyCouponsLiveData.postValue(
                        Resources.success(
                            ApiRepository().applyCouponsApi(token)
                        )

                    )
                } catch (ex: Exception) {
                    applyCouponsLiveData.postValue(Resources.error(ex.localizedMessage?:"", null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun applyCouponsRes(): LiveData<Resources<ApplyCouponsResponse>> {
        return applyCouponsLiveData
    }


    private val removeCouponsLiveData = SingleLiveEvent<Resources<RemoveCouponsResponse>>()






}