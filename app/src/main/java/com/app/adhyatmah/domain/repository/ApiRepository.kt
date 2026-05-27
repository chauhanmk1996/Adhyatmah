package com.app.adhyatmah.domain.repository

import co.paystack.android.api.model.ApiResponse
import com.app.adhyatmah.domain.create_order.creat_order_request.CreaterOrderRequest
import com.app.adhyatmah.domain.create_order.creater_order_response.CreateCODResponse
import com.app.adhyatmah.domain.model.GetLanguagesResponse
import com.app.adhyatmah.domain.model.add_to_bag.add_to_bag_request.AddToBagRequest
import com.app.adhyatmah.domain.model.auth.ForgotPassRequest
import com.app.adhyatmah.domain.model.auth.ForgotPassResponse
import com.app.adhyatmah.domain.model.get_short_collection.request.GetSortedCollectionRequest
import com.app.adhyatmah.domain.model.auth.GetLandingPageResponse
import com.app.adhyatmah.domain.model.auth.GetLoginResponse
import com.app.adhyatmah.domain.model.auth.GetSignUpResponse
import com.app.adhyatmah.domain.model.auth.LoginRequest
import com.app.adhyatmah.domain.model.auth.SignUpRequest
import com.app.adhyatmah.domain.model.bag_response.apply_coupons.apply_coupons_req.ApplyCouponsRequest
import com.app.adhyatmah.domain.model.bag_response.apply_coupons.get_all_apply_coupons.GetAllCouponsResponse
import com.app.adhyatmah.domain.model.bag_response.get_cart_list_data.GetCartListResponse
import com.app.adhyatmah.domain.model.bag_response.increase_qty_request.IncreaseQtyRequest
import com.app.adhyatmah.domain.model.bag_response.increase_quantity.CartQtyIncreaseResponse
import com.app.adhyatmah.domain.model.bag_response.remove_coupon.remove_coupon_request.RemoveCouponRequest
import com.app.adhyatmah.domain.model.bag_response.remove_coupon.remove_coupon_response.RemoveCouponResponse
import com.app.adhyatmah.domain.model.booking_payment.BookingPaymentRequest
import com.app.adhyatmah.domain.model.booking_payment.BookingPaymentResponse
import com.app.adhyatmah.domain.model.create_booking.PanditjiBookingRequest
import com.app.adhyatmah.domain.model.create_booking.PanditjiBookingResponse
import com.app.adhyatmah.domain.model.currency.get_currency.GetCurrencyResponse
import com.app.adhyatmah.domain.model.currency.post_currency.post_currency_request.CurencyPostRequest
import com.app.adhyatmah.domain.model.currency.post_currency.post_currency_response.PostCurrencyResponse
import com.app.adhyatmah.domain.model.customer_all_order_response.cancel_order_request.CancelOrderRequest
import com.app.adhyatmah.domain.model.customer_all_order_response.cancel_order_response.CancelOrderResponse
import com.app.adhyatmah.domain.model.customer_all_order_response.customer_order_details.GetMyOrderDetailsResponse
import com.app.adhyatmah.domain.model.delete_account.delete_request.DeleteRequest
import com.app.adhyatmah.domain.model.delete_account.delete_response.DeleteResponse
import com.app.adhyatmah.domain.model.delete_address.request_Address.DeleteAddressRequest
import com.app.adhyatmah.domain.model.delete_address.response_delete_address.DeleteAddressResponse
import com.app.adhyatmah.domain.model.faq.FAQResponse
import com.app.adhyatmah.domain.model.fetch_wish_data.FetchWishListResponse
import com.app.adhyatmah.domain.model.filter.filter_request.FilterRequest
import com.app.adhyatmah.domain.model.filter.getFilter.GetFilterResponse
import com.app.adhyatmah.domain.model.get_services.GetServicesResponse
import com.app.adhyatmah.domain.model.logout.LogOutRequest
import com.app.adhyatmah.domain.model.privacy_policy.TermPrivacyResponse
import com.app.adhyatmah.domain.model.profile.add_address.AddAddressRequest
import com.app.adhyatmah.domain.model.profile.add_address.CreateAddressResponse
import com.app.adhyatmah.domain.model.profile.edit_profile.edit_profile_request.EditProfileRequest
import com.app.adhyatmah.domain.model.profile.get_img_profile.GetProfileImgResponse
import com.app.adhyatmah.domain.model.profile.get_profile.GetProfileResponse
import com.app.adhyatmah.domain.model.profile.manage_address.ManageAddressRequest
import com.app.adhyatmah.domain.model.registration.RegistrationModel
import com.app.adhyatmah.domain.model.search_list_response.search_list_request.SearchListRequest
import com.app.adhyatmah.domain.model.send_otp.LoginWithMobileRequest
import com.app.adhyatmah.domain.model.send_otp.SendOtpModel
import com.app.adhyatmah.domain.model.shipping_response.ShippingUrlResponse
import com.app.adhyatmah.domain.model.view_all_product.request.ViewAllProductRequest
import com.app.adhyatmah.domain.model.view_all_product.response.ViewAllProductResponse
import com.app.adhyatmah.domain.model.wish_list.remove_wish_list.RemoveWishListResponse
import com.app.adhyatmah.domain.model.wish_list.wish_list_request.AddWishListRequest
import com.app.adhyatmah.domain.model.wish_list.wish_list_response.AddWishListResponse
import com.app.adhyatmah.domain.model.youtube_response.YoutubeResponse
import com.app.adhyatmah.payment.message_sms.sms_request.SMSRequest
import com.app.adhyatmah.payment.message_sms.sms_response.SMSResponse
import com.app.adhyatmah.payment.paymentIniRequest.PaymentIniRequest
import com.app.adhyatmah.payment.payment_clear_order_list_resp.PaymentSuccessClearAllOrderResp
import com.app.adhyatmah.payment.payment_clear_order_request.PaymentSuccessClearOrderRequest
import com.app.adhyatmah.payment.payment_varify_response.PaymentVerifyResponse
import com.app.adhyatmah.payment.payment_verify_request.PaymentVerifyRequest
import com.app.panditji.data.model.get_booking.GetBookingResponse
import com.app.adhyatmah.domain.model.update_booking_status.UpdateBookingStatusRequest
import com.app.adhyatmah.domain.model.verify_otp.VerifyOtpResponse
import com.app.adhyatmah.payment.payment_initialize_response.PaymentIniResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody


class ApiRepository {

    private val service = RetrofitBuilder.apiService


    suspend fun getHomeCollectionApiApi(token: String) = service.getHomeCollection(token)

    suspend fun getHomeBannerApi(/*token: String*/) = service.getHomeBanner()

    suspend fun getHomeBlogApi(/*token: String*/) = service.getHomeBlog()

    suspend fun getHomeYoutubeUrlApi() = service.getYoutubeUrl()

    suspend fun getHomeMenuApi(/*token: String*/) = service.getHomeMenu()

    suspend fun getProductDetailsApi(id: String, token: String) =
        service.getProductAttributes(id, token)

    suspend fun getAllCatDetailsApi(page: Int, limit: Int) = service.getAllCollections(page, limit)

    suspend fun getAllReviewList(productId: String, page: Int, limit: Int) =
        service.getReviewData(productId, page, limit)

    suspend fun getViewAllDataApi(
        token: String,
        handle: ViewAllProductRequest,
    ): ViewAllProductResponse = service.getViewAllData(token, handle)

    suspend fun addWishListApi(request: AddWishListRequest): AddWishListResponse =
        service.addWishListData(request)

    suspend fun getWishListApi(token: String): FetchWishListResponse =
        service.getWishListData(token)

    suspend fun getLoginAPIs(request: LoginRequest): GetSignUpResponse = service.login(request)
    suspend fun getLoginWIthMobileApi(request: LoginWithMobileRequest): GetLoginResponse =
        service.loginWithMobile(request)

    suspend fun getVerifyOtpApi(request: RegistrationModel): GetSignUpResponse =
        service.verifyOtp(request)

    suspend fun getResendOtpApi(request: SendOtpModel): ApiResponse = service.reSendOtp(request)
    suspend fun getLandingAPIs(): GetLandingPageResponse = service.getLandingPage()

    suspend fun getSignUpAPIs(request: SignUpRequest): GetSignUpResponse = service.signUp(request)

    suspend fun getShortCollectionApi(request: GetSortedCollectionRequest): ViewAllProductResponse =
        service.getSortedCollection(request)

    suspend fun getPrivacyAPIs(): TermPrivacyResponse = service.getPolicies()

    suspend fun getLogOutAPIs(token: LogOutRequest): GetLoginResponse = service.logout(token)

    suspend fun getForgotApi(request: ForgotPassRequest): ForgotPassResponse =
        service.forgotPass(request)

    suspend fun filterApi(request: FilterRequest): ViewAllProductResponse =
        service.filterCollection(request)

    suspend fun removeWishList(request: AddWishListRequest): RemoveWishListResponse =
        service.removeWishList(request)

    suspend fun getContactUsApi(/*token: String*/) = service.getContactUs()

    suspend fun getCreateAddressApi(request: AddAddressRequest) = service.createCustomerAdd(request)

    suspend fun getCustomerAddApi(token: ManageAddressRequest) = service.getCustomerAdd()

    suspend fun getCartListApi(token: String): GetCartListResponse =
        service.getAllCartListData(token)

    suspend fun getCouponListApi(): GetAllCouponsResponse = service.getAllCoupons()

    suspend fun getIncreaseQtyApi(request: IncreaseQtyRequest): CartQtyIncreaseResponse =
        service.updateQty(request)


    suspend fun customerAllOrdersApi(request: ManageAddressRequest) =
        service.customerAllOrders(request)

    suspend fun applyCouponsApi(request: ApplyCouponsRequest) = service.applyCoupons(request)

    suspend fun searchListApi(request: SearchListRequest) = service.searchList(request)

    suspend fun searchTypeListApi() = service.getSearchTypeList()
    suspend fun getPaymentMethodApi() = service.getPaymentMethod()

    suspend fun addToBagApi(request: AddToBagRequest) = service.addToBag(request)
    suspend fun deleteAddressAPI(request: DeleteAddressRequest): DeleteAddressResponse =
        service.deleteCustomAddress(request)

    suspend fun editAddressAPI(request: AddAddressRequest): CreateAddressResponse =
        service.editCustomAddress(request)

    suspend fun getProfileAPI(request: String): GetProfileResponse = service.getProfile(request)
    suspend fun editProfileAPI(request: EditProfileRequest): GetProfileResponse =
        service.editProfile(request)

    suspend fun getFilterAPI(): GetFilterResponse = service.getFilter()

    suspend fun uploadImage(file: MultipartBody.Part, customerId: RequestBody): GetProfileResponse {
        return service.upLoadImg(file, customerId)
    }

    suspend fun getProfileImage(customerId: String): GetProfileImgResponse =
        service.getProfileImg(customerId)

    suspend fun getOrderDetailsAPIs(orderId: String): GetMyOrderDetailsResponse =
        service.getOrderDetails(orderId)

    suspend fun cancelOrderAPIs(request: CancelOrderRequest): CancelOrderResponse =
        service.cancelOrderApi(request)

    suspend fun createCODPaymentAPI(request: CreaterOrderRequest): CreateCODResponse =
        service.createCODOrder(request)

    suspend fun payStackPaymentAPI(request: PaymentIniRequest): PaymentIniResponse =
        service.paymentIniApi(request)

    suspend fun paymentVerifyAPI(request: PaymentVerifyRequest): PaymentVerifyResponse =
        service.paymentVerifyApi(request)

    suspend fun faqAPI(role: String): FAQResponse = service.faq(role)

    suspend fun smsAPi(request: SMSRequest): SMSResponse = service.smsApi(request)
    suspend fun paymentSucClearOrderAPI(accessToken: PaymentSuccessClearOrderRequest): PaymentSuccessClearAllOrderResp =
        service.paymentSuccessClearAllOrderApi(accessToken)

    suspend fun removeCouponAPI(request: RemoveCouponRequest): RemoveCouponResponse =
        service.removeCoupon(request)

    suspend fun getcurrencyAPI(): GetCurrencyResponse = service.getCurrency()
    suspend fun postcurrencyAPI(request: CurencyPostRequest): PostCurrencyResponse =
        service.postCurrency(request)

    suspend fun deleteAccountAPI(request: DeleteRequest): DeleteResponse =
        service.deleteAccountApi(request)

    suspend fun shippingUrlAPI(): ShippingUrlResponse = service.getShippingUrl()
    suspend fun youtubeResponse(): YoutubeResponse = service.getYoutubeVideo()
    suspend fun getIndianStateApi() = service.getIndianState()

    suspend fun trendingSectionApi() = service.trendingSectionApi()

    suspend fun homeDataApi() = service.homeDataApi()

    // Pandit List

    suspend fun getPanditListApi(name: String? = null, serviceName: String? = null) =
        service.getPanditList(name, serviceName)

    suspend fun panditjiBookingAPI(request: PanditjiBookingRequest): PanditjiBookingResponse =
        service.createBookingApi(request)

    suspend fun getPanditjiServicesApi(panditId: String): GetServicesResponse =
        service.getPanditjiServices(panditId)

    suspend fun getBookingHistoryApi(type: String): GetBookingResponse = service.getBookings(type)

    suspend fun updateBookingStatusApi(
        type: String,
        request: UpdateBookingStatusRequest,
    ): GetBookingResponse = service.updateBookingStatus(type, request)

    suspend fun createBookingPaymentApi(request: BookingPaymentRequest): BookingPaymentResponse =
        service.createBookingPayment(request)

    suspend fun getAllLanguagesApi(): GetLanguagesResponse = service.getAllLanguages()


}