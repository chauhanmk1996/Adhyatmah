package com.app.adhyatmah.presentation.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import co.paystack.android.api.model.ApiResponse
import com.app.adhyatmah.domain.model.auth.ForgotPassRequest
import com.app.adhyatmah.domain.model.auth.ForgotPassResponse
import com.app.adhyatmah.domain.model.auth.GetLandingPageResponse
import com.app.adhyatmah.domain.model.auth.GetLoginResponse
import com.app.adhyatmah.domain.model.auth.GetSignUpResponse
import com.app.adhyatmah.domain.model.auth.LoginRequest
import com.app.adhyatmah.domain.model.auth.SignUpRequest
import com.app.adhyatmah.domain.model.profile.edit_profile.edit_profile_request.EditProfileRequest
import com.app.adhyatmah.domain.model.profile.get_img_profile.GetProfileImgResponse
import com.app.adhyatmah.domain.model.profile.get_profile.GetProfileResponse
import com.app.adhyatmah.domain.model.registration.RegistrationModel
import com.app.adhyatmah.domain.model.send_otp.LoginWithMobileRequest
import com.app.adhyatmah.domain.model.send_otp.SendOtpModel
import com.app.adhyatmah.domain.model.verify_otp.VerifyOtpResponse
import com.app.adhyatmah.domain.repository.ApiRepository
import com.app.adhyatmah.utils.common_utils.Resources
import com.app.adhyatmah.utils.common_utils.SingleLiveEvent
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class AuthViewModel @Inject constructor(application: Application) : AndroidViewModel(application){

    private val loginLiveData = SingleLiveEvent<Resources<GetSignUpResponse>>()
    private val loginWithMobileLiveData = SingleLiveEvent<Resources<GetLoginResponse>>()
    private val getProfileLiveData = SingleLiveEvent<Resources<GetProfileResponse>>()
    private val getEditLiveData = SingleLiveEvent<Resources<GetProfileResponse>>()
    private val getGetProfileImgLiveData = SingleLiveEvent<Resources<GetProfileImgResponse>>()
    private val getVerifyOtpLiveData = SingleLiveEvent<Resources<GetSignUpResponse>>()
    private val getResendOtpLiveData = SingleLiveEvent<Resources<ApiResponse>>()

    private val uploadProfileImgLiveData = SingleLiveEvent<Resources<GetProfileResponse>>()

    fun getLoginData(request: LoginRequest) {
        try {
            loginLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {

                    loginLiveData.postValue(
                        Resources.success(
                            ApiRepository().getLoginAPIs(
                                request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    loginLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
    fun hitLoginWithMobileData(request: LoginWithMobileRequest) {
        try {
            loginWithMobileLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {

                    loginWithMobileLiveData.postValue(
                        Resources.success(
                            ApiRepository().getLoginWIthMobileApi(
                                request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    loginWithMobileLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
    fun getVerifyOtpData(): LiveData<Resources<GetSignUpResponse>> {
        return getVerifyOtpLiveData
    }
    fun hitVerifyOtpData(request: RegistrationModel) {
        try {
            getVerifyOtpLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {

                    getVerifyOtpLiveData.postValue(
                        Resources.success(
                            ApiRepository().getVerifyOtpApi(
                                request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    getVerifyOtpLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
    fun getResendOtpData(): LiveData<Resources<ApiResponse>> {
        return getResendOtpLiveData
    }
    fun hitResendOtpData(request: SendOtpModel) {
        try {
            getResendOtpLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {

                    getResendOtpLiveData.postValue(
                        Resources.success(
                            ApiRepository().getResendOtpApi(
                                request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    getResendOtpLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
    fun getLoginWithMobileData(): LiveData<Resources<GetLoginResponse>> {
        return loginWithMobileLiveData
    }

    fun hitAPIProfileData(token: String) {
        try {
            getProfileLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {

                    getProfileLiveData.postValue(
                        Resources.success(
                            ApiRepository().getProfileAPI(
                                token
                            )
                        )
                    )
                } catch (ex: Exception) {
                    getProfileLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
    fun hitAPIProfileImageData(customerId: String){
        try {
            getGetProfileImgLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {

                    getGetProfileImgLiveData.postValue(
                        Resources.success(
                            ApiRepository().getProfileImage(customerId
                            )
                        )
                    )
                } catch (ex: Exception) {
                    getGetProfileImgLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
   /* fun hitEditProfileData(request: EditProfileRequest) {
        try {
            getEditLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {

                    getEditLiveData.postValue(
                        Resources.success(
                            ApiRepository().editProfileAPI(
                                request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    getEditLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
*/
    fun hitEditProfileData(request: EditProfileRequest) {
        getEditLiveData.postValue(Resources.loading(null))
        viewModelScope.launch {
            try {
                val response = ApiRepository().editProfileAPI(request)
                getEditLiveData.postValue(Resources.success(response))
            } catch (ex: HttpException) {
                val errorBody = ex.response()?.errorBody()?.string()
                val errorMessage = try {
                    val json = JSONObject(errorBody ?: "")
                    json.optString("message") +
                            (json.optJSONObject("payload")
                                ?.optJSONObject("errors")
                                ?.optJSONArray("phone")
                                ?.optString(0) ?: "")
                } catch (e: Exception) {
                    "Validation error"
                }
                getEditLiveData.postValue(Resources.error(errorMessage, null))
            } catch (ex: Exception) {
                getEditLiveData.postValue(Resources.error(ex.localizedMessage ?: "Unknown error", null))
            }
        }
    }



    fun uploadImageToServer(file: MultipartBody.Part, customerId: RequestBody) {
        uploadProfileImgLiveData.postValue(Resources.loading(null))
        viewModelScope.launch {
            try {
                val response = ApiRepository().uploadImage(file, customerId)
                uploadProfileImgLiveData.postValue(Resources.success(response))
            } catch (e: Exception) {
                uploadProfileImgLiveData.postValue(Resources.error(e.localizedMessage ?: "Error occurred", null))
            }
        }
    }




    fun getLoginData(): LiveData<Resources<GetSignUpResponse>> {
        return loginLiveData
    }
    fun uploadProfileImgData(): LiveData<Resources<GetProfileResponse>> {
        return uploadProfileImgLiveData
    }
    fun getProfileData(): LiveData<Resources<GetProfileResponse>> {
        return getProfileLiveData
    }
    fun getEditProfileData(): LiveData<Resources<GetProfileResponse>> {
        return getEditLiveData
    }
    fun getProfileImgData(): LiveData<Resources<GetProfileImgResponse>> {
        return getGetProfileImgLiveData
    }


    private val landingPageLiveData = SingleLiveEvent<Resources<GetLandingPageResponse>>()
    fun getLandingPagesData() {
        try {
            landingPageLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {

                    landingPageLiveData.postValue(
                        Resources.success(
                            ApiRepository().getLandingAPIs()
                        )
                    )
                } catch (ex: Exception) {
                    landingPageLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getLandingPageData(): LiveData<Resources<GetLandingPageResponse>> {
        return landingPageLiveData
    }

    private val signUpLiveData = SingleLiveEvent<Resources<GetSignUpResponse>>()

    fun getSignUpData(request: SignUpRequest) {
        try {
            signUpLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {

                    signUpLiveData.postValue(
                        Resources.success(
                            ApiRepository().getSignUpAPIs(
                                request
                            )
                        )
                    )
                } catch (ex: Exception) {
                    signUpLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getSignupData(): LiveData<Resources<GetSignUpResponse>> {
        return signUpLiveData
    }


    private val forgotLiveData = SingleLiveEvent<Resources<ForgotPassResponse>>()

    fun getForgotData(request: ForgotPassRequest) {
        try {
            forgotLiveData.postValue(Resources.loading(null))
            viewModelScope.launch {
                try {

                    forgotLiveData.postValue(
                        Resources.success(
                            ApiRepository().getForgotApi(request)
                        )
                    )
                } catch (ex: Exception) {
                    forgotLiveData.postValue(Resources.error(ex.localizedMessage, null))

                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getForgotPassData(): LiveData<Resources<ForgotPassResponse>> {
        return forgotLiveData
    }



}