package com.app.adhyatmah.presentation.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.adhyatmah.domain.model.customer_all_order_response.cancel_order_request.CancelOrderRequest
import com.app.adhyatmah.domain.model.customer_all_order_response.cancel_order_response.CancelOrderResponse
import com.app.adhyatmah.domain.model.customer_all_order_response.customer_all_orders.CustomerAllOrdersResponse
import com.app.adhyatmah.domain.model.customer_all_order_response.customer_order_details.GetMyOrderDetailsResponse
import com.app.adhyatmah.domain.model.delete_address.response_delete_address.DeleteAddressResponse
import com.app.adhyatmah.domain.model.profile.add_address.AddAddressRequest
import com.app.adhyatmah.domain.model.profile.add_address.CreateAddressResponse
import com.app.adhyatmah.domain.model.profile.contact_us.ContactUsResponse
import com.app.adhyatmah.domain.model.profile.manage_address.CustomerAddressResponse
import com.app.adhyatmah.domain.model.profile.manage_address.ManageAddressRequest
import com.app.adhyatmah.domain.model.shipping_response.ShippingUrlResponse
import com.app.adhyatmah.domain.repository.ApiRepository
import com.app.adhyatmah.utils.common_utils.Resources
import com.app.adhyatmah.utils.common_utils.SingleLiveEvent
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    private val contactUsLiveData = SingleLiveEvent<Resources<ContactUsResponse>>()
    private val createAddressLiveData = SingleLiveEvent<Resources<CreateAddressResponse>>()
    private val deleteAddressLiveData = SingleLiveEvent<Resources<DeleteAddressResponse>>()
    private val indianStateLiveData = SingleLiveEvent<Resources<ShippingUrlResponse>>()
    private val getAddressLiveData = SingleLiveEvent<Resources<CustomerAddressResponse>>()

    fun getContactUsData() {
        try {
            contactUsLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    contactUsLiveData.postValue(
                        Resources.success(
                            ApiRepository().getContactUsApi()
                        )
                    )
                } catch (ex: Exception) {
                    contactUsLiveData.postValue(Resources.error(ex.localizedMessage ?: "", null))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
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

    fun getCreateAddressData(request: AddAddressRequest) {
        try {
            createAddressLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    createAddressLiveData.postValue(
                        Resources.success(
                            ApiRepository().getCreateAddressApi(request)
                        )
                    )
                } catch (ex: Exception) {
                    createAddressLiveData.postValue(
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

    fun getEditAddressData(request: AddAddressRequest) {
        try {
            createAddressLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    createAddressLiveData.postValue(
                        Resources.success(
                            ApiRepository().editAddressAPI(request)
                        )
                    )
                } catch (ex: Exception) {
                    createAddressLiveData.postValue(
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

    fun hitDeleteAddressApi(addressId: String) {
        try {
            deleteAddressLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    deleteAddressLiveData.postValue(
                        Resources.success(
                            ApiRepository().deleteAddressAPI(addressId)
                        )
                    )
                } catch (ex: Exception) {
                    deleteAddressLiveData.postValue(
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

    fun hitIndianStateApi() {
        try {
            indianStateLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    indianStateLiveData.postValue(
                        Resources.success(
                            ApiRepository().getIndianStateApi()
                        )
                    )
                } catch (ex: Exception) {
                    indianStateLiveData.postValue(Resources.error(ex.localizedMessage ?: "", null))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getCreateAddressRes(): LiveData<Resources<CreateAddressResponse>> {
        return createAddressLiveData
    }

    fun getContactUsRes(): LiveData<Resources<ContactUsResponse>> {
        return contactUsLiveData
    }

    fun getCustomerAddressRes(): LiveData<Resources<CustomerAddressResponse>> {
        return getAddressLiveData
    }

    fun getDeleteAddressRes(): LiveData<Resources<DeleteAddressResponse>> {
        return deleteAddressLiveData
    }

    fun getIndianStateRes(): LiveData<Resources<ShippingUrlResponse>> {
        return indianStateLiveData
    }

    private val customerOrdersLiveData = SingleLiveEvent<Resources<CustomerAllOrdersResponse>>()

    fun getCustomerOrdersData(token: ManageAddressRequest) {
        try {
            customerOrdersLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    customerOrdersLiveData.postValue(
                        Resources.success(
                            ApiRepository().customerAllOrdersApi(token)
                        )
                    )
                } catch (ex: Exception) {
                    customerOrdersLiveData.postValue(
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

    fun getCustomerOrdersRes(): LiveData<Resources<CustomerAllOrdersResponse>> {
        return customerOrdersLiveData
    }

    private val myOrderDetailsLiveData = SingleLiveEvent<Resources<GetMyOrderDetailsResponse>>()

    fun getOrderDetailsData(orderId: String) {
        try {
            myOrderDetailsLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    myOrderDetailsLiveData.postValue(
                        Resources.success(
                            ApiRepository().getOrderDetailsAPIs(orderId)
                        )
                    )
                } catch (ex: Exception) {
                    myOrderDetailsLiveData.postValue(
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

    fun getOrdersDetailsRes(): LiveData<Resources<GetMyOrderDetailsResponse>> {
        return myOrderDetailsLiveData
    }

    private val cancelOrderLiveData = SingleLiveEvent<Resources<CancelOrderResponse>>()

    fun cancelOrderData(request: CancelOrderRequest) {
        try {
            myOrderDetailsLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    cancelOrderLiveData.postValue(
                        Resources.success(
                            ApiRepository().cancelOrderAPIs(request)
                        )
                    )
                } catch (ex: Exception) {
                    cancelOrderLiveData.postValue(Resources.error(ex.localizedMessage ?: "", null))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun cancelOrderRes(): LiveData<Resources<CancelOrderResponse>> {
        return cancelOrderLiveData
    }

    private val shippingUrlLiveData = SingleLiveEvent<Resources<ShippingUrlResponse>>()

    fun shippingUrlData() {
        try {
            shippingUrlLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    shippingUrlLiveData.postValue(
                        Resources.success(
                            ApiRepository().shippingUrlAPI()
                        )
                    )
                } catch (ex: Exception) {
                    shippingUrlLiveData.postValue(Resources.error(ex.localizedMessage ?: "", null))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun shippingUrlRes(): LiveData<Resources<ShippingUrlResponse>> {
        return shippingUrlLiveData
    }
}