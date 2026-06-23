package com.app.adhyatmah.presentation.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.adhyatmah.domain.create_order.creat_order_request.CreaterOrderRequest
import com.app.adhyatmah.domain.create_order.creater_order_response.CreateCODResponse
import com.app.adhyatmah.domain.model.faq.FAQResponse
import com.app.adhyatmah.domain.repository.ApiRepository
import com.app.adhyatmah.payment.message_sms.sms_response.SMSResponse
import com.app.adhyatmah.payment.payment.PaymentTypeResponse
import com.app.adhyatmah.payment.paymentIniRequest.PaymentIniRequest
import com.app.adhyatmah.payment.payment_clear_order_list_resp.PaymentSuccessClearAllOrderResp
import com.app.adhyatmah.payment.payment_clear_order_request.PaymentSuccessClearOrderRequest
import com.app.adhyatmah.payment.payment_initialize_response.PaymentIniResponse
import com.app.adhyatmah.payment.payment_varify_response.PaymentVerifyResponse
import com.app.adhyatmah.utils.common_utils.Resources
import com.app.adhyatmah.utils.common_utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    private val codLiveData = SingleLiveEvent<Resources<CreateCODResponse>>()
    private val faqLiveData = SingleLiveEvent<Resources<FAQResponse>>()
    private val paymentMethodLiveData = SingleLiveEvent<Resources<PaymentTypeResponse>>()

    fun getCOD(): LiveData<Resources<CreateCODResponse>> {
        return codLiveData
    }

    fun hipAPICreateCODOrder(request: CreaterOrderRequest) {
        try {
            codLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    codLiveData.postValue(
                        Resources.success(
                            ApiRepository().createCODPaymentAPI(request)
                        )
                    )
                } catch (ex: Exception) {
                    codLiveData.postValue(Resources.error(ex.localizedMessage ?: "", null))
                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private val payStackLiveData = SingleLiveEvent<Resources<PaymentIniResponse>>()

    fun createPayStackOrder(request: PaymentIniRequest) {
        try {
            payStackLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    payStackLiveData.postValue(
                        Resources.success(
                            ApiRepository().payStackPaymentAPI(request)
                        )
                    )
                } catch (ex: Exception) {
                    payStackLiveData.postValue(Resources.error(ex.localizedMessage ?: "", null))
                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun hitFAQApi(role: String) {
        try {
            faqLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    faqLiveData.postValue(
                        Resources.success(
                            ApiRepository().faqAPI(role)
                        )
                    )
                } catch (ex: Exception) {
                    faqLiveData.postValue(Resources.error(ex.localizedMessage ?: "", null))
                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getPayStackApisResponse(): LiveData<Resources<PaymentIniResponse>> {
        return payStackLiveData
    }

    fun getFAQApiResponse(): LiveData<Resources<FAQResponse>> {
        return faqLiveData
    }

    fun getPaymentMethodApiResponse(): LiveData<Resources<PaymentTypeResponse>> {
        return paymentMethodLiveData
    }

    private val paymentVerifyLiveData = SingleLiveEvent<Resources<PaymentVerifyResponse>>()

    fun hitPaymentTypeAPI() {
        try {
            paymentMethodLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {

                    paymentMethodLiveData.postValue(
                        Resources.success(

                            ApiRepository().getPaymentMethodApi()
                        )

                    )
                } catch (ex: Exception) {
                    paymentMethodLiveData.postValue(
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

    private val paymentSuOrderClearLiveData =
        SingleLiveEvent<Resources<PaymentSuccessClearAllOrderResp>>()

    fun paymentSuOrderClearAPIs(accessToken: PaymentSuccessClearOrderRequest) {
        try {
            paymentSuOrderClearLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {
                    paymentSuOrderClearLiveData.postValue(
                        Resources.success(
                            ApiRepository().paymentSucClearOrderAPI(accessToken)
                        )
                    )
                } catch (ex: Exception) {
                    paymentSuOrderClearLiveData.postValue(
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

    fun getPaymentSuOrderClearRes(): LiveData<Resources<PaymentSuccessClearAllOrderResp>> {
        return paymentSuOrderClearLiveData
    }
}