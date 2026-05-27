package com.app.adhyatmah.domain.repository

import co.paystack.android.api.model.ApiResponse
import com.app.adhyatmah.domain.create_order.creat_order_request.CreaterOrderRequest
import com.app.adhyatmah.domain.create_order.creater_order_response.CreateCODResponse
import com.app.adhyatmah.domain.model.wish_list.wish_list_request.AddWishListRequest
import com.app.adhyatmah.domain.model.AllCategoryListResponse
import com.app.adhyatmah.domain.model.GetLanguagesResponse
import com.app.adhyatmah.domain.model.HomeResponse
import com.app.adhyatmah.domain.model.ProductReviewListResponse
import com.app.adhyatmah.domain.model.TrendingSectionResponse
import com.app.adhyatmah.domain.model.add_to_bag.add_to_bag_request.AddToBagRequest
import com.app.adhyatmah.domain.model.add_to_bag.add_to_bag_response.AddtoBagResponse
import com.app.adhyatmah.domain.model.auth.ForgotPassRequest
import com.app.adhyatmah.domain.model.auth.ForgotPassResponse
import com.app.adhyatmah.domain.model.auth.GetLandingPageResponse
import com.app.adhyatmah.domain.model.auth.GetLoginResponse
import com.app.adhyatmah.domain.model.auth.GetSignUpResponse
import com.app.adhyatmah.domain.model.auth.LoginRequest
import com.app.adhyatmah.domain.model.auth.SignUpRequest
import com.app.adhyatmah.domain.model.bag_response.apply_coupons.apply_coupons.ApplyCouponsResponse
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
import com.app.adhyatmah.domain.model.customer_all_order_response.customer_all_orders.CustomerAllOrdersResponse
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
import com.app.adhyatmah.domain.model.get_short_collection.request.GetSortedCollectionRequest
import com.app.adhyatmah.domain.model.home_banner_response.HomeBannerResponse
import com.app.adhyatmah.domain.model.home_blog_response.HomeBlogResponse
import com.app.adhyatmah.domain.model.home_collection_Response.HomeCollectionResponse
import com.app.adhyatmah.domain.model.home_menu_response.HomeMenuResponse
import com.app.adhyatmah.domain.model.logout.LogOutRequest
import com.app.adhyatmah.domain.model.pandit_list.get_pandit_list.GetPanditResponse
import com.app.adhyatmah.domain.model.privacy_policy.TermPrivacyResponse
import com.app.adhyatmah.domain.model.product_detail_response.ProductDetailResponse
import com.app.adhyatmah.domain.model.profile.add_address.AddAddressRequest
import com.app.adhyatmah.domain.model.profile.add_address.CreateAddressResponse
import com.app.adhyatmah.domain.model.profile.contact_us.ContactUsResponse
import com.app.adhyatmah.domain.model.profile.edit_profile.edit_profile_request.EditProfileRequest
import com.app.adhyatmah.domain.model.profile.get_img_profile.GetProfileImgResponse
import com.app.adhyatmah.domain.model.profile.get_profile.GetProfileResponse
import com.app.adhyatmah.domain.model.profile.manage_address.CustomerAddressResponse
import com.app.adhyatmah.domain.model.profile.manage_address.ManageAddressRequest
import com.app.adhyatmah.domain.model.registration.RegistrationModel
import com.app.adhyatmah.domain.model.search_list_response.get_search_types_response.GetSearchTypeResponse
import com.app.adhyatmah.domain.model.search_list_response.search_list_api_response.GetSearchListResponse
import com.app.adhyatmah.domain.model.search_list_response.search_list_request.SearchListRequest
import com.app.adhyatmah.domain.model.send_otp.LoginWithMobileRequest
import com.app.adhyatmah.domain.model.send_otp.SendOtpModel
import com.app.adhyatmah.domain.model.shipping_response.ShippingUrlResponse
import com.app.adhyatmah.domain.model.view_all_product.request.ViewAllProductRequest
import com.app.adhyatmah.domain.model.view_all_product.response.ViewAllProductResponse
import com.app.adhyatmah.domain.model.wish_list.remove_wish_list.RemoveWishListResponse
import com.app.adhyatmah.domain.model.wish_list.wish_list_response.AddWishListResponse
import com.app.adhyatmah.domain.model.youtube_url_response.YoutubeUrlResponse
import com.app.adhyatmah.domain.model.youtube_response.YoutubeResponse
import com.app.adhyatmah.payment.message_sms.sms_request.SMSRequest
import com.app.adhyatmah.payment.message_sms.sms_response.SMSResponse
import com.app.adhyatmah.payment.payment.PaymentTypeResponse
import com.app.adhyatmah.payment.paymentIniRequest.PaymentIniRequest
import com.app.adhyatmah.payment.payment_clear_order_list_resp.PaymentSuccessClearAllOrderResp
import com.app.adhyatmah.payment.payment_clear_order_request.PaymentSuccessClearOrderRequest
import com.app.adhyatmah.payment.payment_varify_response.PaymentVerifyResponse
import com.app.adhyatmah.payment.payment_verify_request.PaymentVerifyRequest
import com.app.panditji.data.model.get_booking.GetBookingResponse
import com.app.adhyatmah.domain.model.update_booking_status.UpdateBookingStatusRequest
import com.app.adhyatmah.payment.payment_initialize_response.PaymentIniResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @GET("getHomepageCollections")
    suspend fun getHomeCollection(@Query ("accessToken") token: String): HomeCollectionResponse

    @GET("getBanner")
    suspend fun getHomeBanner(/*@Header ("Authorization") token: String*/): HomeBannerResponse

     @GET("blogs")
    suspend fun getHomeBlog(/*@Header ("Authorization") token: String*/): HomeBlogResponse


    @GET("allMenus")
    suspend fun getHomeMenu(/*@Header ("Authorization") token: String*/): HomeMenuResponse

    @GET("getProductAttributes/")
    suspend fun getProductAttributes(
        @Query("id") id: String,
        @Query("accessToken") token: String
    ): ProductDetailResponse


    @GET("allCollections")
    suspend fun getAllCollections(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): AllCategoryListResponse

    @GET("getReviewData")
    suspend fun getReviewData(
        @Query("id") productId: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): ProductReviewListResponse

    @POST("getSortedCollection")
    suspend fun getSortedCollection(
        @Body productId: GetSortedCollectionRequest,
    ): ViewAllProductResponse

    @POST("getViewAllData")
    suspend fun getViewAllData(
        @Query("accessToken") token: String,
        @Body handle : ViewAllProductRequest
    ): ViewAllProductResponse

   /* @POST("getMasterCollectionProducts")
    suspend fun getViewAllData(
        @Query("accessToken") token: String,
        @Body handle : ViewAllProductRequest
    ): ViewAllProductResponse
*/
    @POST("addToWishlist")
    suspend fun addWishListData(@Body request: AddWishListRequest): AddWishListResponse

    @GET("getWishlist")
    suspend fun getWishListData(
        @Query("accessToken") token: String
    ): FetchWishListResponse


    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): GetSignUpResponse

    @POST("createCustomer")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): GetSignUpResponse

    @GET("getLandingPage")
    suspend fun getLandingPage(): GetLandingPageResponse

    @POST("forgotPassword")
    suspend fun forgotPass(
        @Body request: ForgotPassRequest
    ): ForgotPassResponse

     @GET("getPolicies")
    suspend fun getPolicies(): TermPrivacyResponse

     @POST("logout")
      suspend fun logout(@Body  accessToken: LogOutRequest): GetLoginResponse

       @POST("filterCollection")
      suspend fun filterCollection(@Body  request: FilterRequest): ViewAllProductResponse


      @POST("removeFromWishlist")
      suspend fun removeWishList(@Body  request: AddWishListRequest): RemoveWishListResponse

    @GET("getContactInfo")
    suspend fun getContactUs(): ContactUsResponse
     @GET("getPaymentMethods")
    suspend fun getPaymentMethod(): PaymentTypeResponse

    @POST("createCustomerAddress")
    suspend fun createCustomerAdd(@Body request: AddAddressRequest): CreateAddressResponse

    @GET("getCustomerAddresses")
    suspend fun getCustomerAdd(): CustomerAddressResponse

 @POST("createCart")
    suspend fun addToBag(
        @Body accessToken: AddToBagRequest
 ): AddtoBagResponse

    @GET("getCart")
    suspend fun getAllCartListData(
        @Query("accessToken") token: String
    ): GetCartListResponse

    @GET("getCoupons")
    suspend fun getAllCoupons(): GetAllCouponsResponse

    @POST("updateCart")
    suspend fun updateQty(
        @Body request: IncreaseQtyRequest
    ): CartQtyIncreaseResponse

  @POST("deleteCustomerAddress")
    suspend fun deleteCustomAddress(
        @Body request: DeleteAddressRequest
    ): DeleteAddressResponse

      @PUT("updateCustomerAddress")
       suspend fun editCustomAddress(@Body request: AddAddressRequest): CreateAddressResponse

//       @GET("getCustomerProfile")
//       suspend fun getProfile(@Query("accessToken") token: String, ): GetProfileResponse

        @GET("getUserProfile")
        suspend fun getProfile(@Query("accessToken") token: String, ): GetProfileResponse



    @POST("updateCustomerProfile")
       suspend fun editProfile(@Body request: EditProfileRequest): GetProfileResponse
    @POST("customerAllOrders")
    suspend fun customerAllOrders(
        @Body request: ManageAddressRequest
    ): CustomerAllOrdersResponse

    @POST("applyCoupon")
    suspend fun applyCoupons(
        @Body request: ApplyCouponsRequest
    ): ApplyCouponsResponse

   @POST("search")
    suspend fun searchList(
        @Body request: SearchListRequest
    ): GetSearchListResponse

     @GET("getSearchTypes")
    suspend fun getSearchTypeList(): GetSearchTypeResponse

       @GET("getFilterCollectionData")
       suspend fun getFilter(): GetFilterResponse

      @POST("createCodOrder")
      suspend fun createCODOrder(@Body request: CreaterOrderRequest): CreateCODResponse


      @Multipart
      @POST("upload")
      suspend fun upLoadImg(
          @Part file: MultipartBody.Part,
          @Part ("customerId") customerId: RequestBody
      ): GetProfileResponse

    @GET("getProfileImage")
    suspend fun getProfileImg(@Query("customerId") customer: String): GetProfileImgResponse

    @GET("getOrderById")
    suspend fun getOrderDetails(
        @Query("orderId") orderId: String
    ): GetMyOrderDetailsResponse

    @POST("initializePayment")
    suspend fun paymentIniApi(
        @Body request: PaymentIniRequest
    ): PaymentIniResponse
    @GET("getFAQs")
    suspend fun faq(@Query("role") role: String): FAQResponse

    @POST("sendSMS")
    suspend fun smsApi(
        @Body request: SMSRequest
    ): SMSResponse
   @POST("verifyPaymentAndCreateOrder")
    suspend fun paymentVerifyApi(
        @Body request: PaymentVerifyRequest
    ): PaymentVerifyResponse

   @POST("clearCartStore")
    suspend fun paymentSuccessClearAllOrderApi(
     @Body accessToken: PaymentSuccessClearOrderRequest
    ): PaymentSuccessClearAllOrderResp
 /*@POST("clearCartStore")
  suspend fun paymentSuccessClearAllOrderApi(@Body accessToken: PaymentSuccessClearOrderRequest): PaymentSuccessClearAllOrderResp
*/

  @POST("removeDiscount")
  suspend fun removeCoupon(@Body request: RemoveCouponRequest): RemoveCouponResponse

 @POST("currencyConveter")
  suspend fun postCurrency(@Body request: CurencyPostRequest): PostCurrencyResponse

  @GET("getCurrency")
  suspend fun getCurrency(): GetCurrencyResponse

    @GET("getYoutubeUrl")
    suspend fun getYoutubeUrl(): YoutubeUrlResponse

    @POST("cancelCustomerOrder")
    suspend fun cancelOrderApi(
        @Body request: CancelOrderRequest
    ): CancelOrderResponse


 @POST("deleteCustomer")
    suspend fun deleteAccountApi(
        @Body request: DeleteRequest
    ): DeleteResponse

    @GET("getShippingUrls")
    suspend fun getShippingUrl(): ShippingUrlResponse


    @GET("getYoutubeUrl")
    suspend fun getYoutubeVideo(): YoutubeResponse

     @GET("getIndianStates")
    suspend fun getIndianState(): ShippingUrlResponse



   // Pandit ji API

    @GET("getAllPandit")
    suspend fun getPanditList(
        @Query("name") name: String? = null,
        @Query("serviceName") serviceName: String? = null,
        ): GetPanditResponse

    @GET("getHomepageCollections")
    suspend fun trendingSectionApi(
    ): TrendingSectionResponse

    @GET("getHomepagePoojaServices")
    suspend fun homeDataApi(
    ): HomeResponse

    @POST("createBooking")
    suspend fun createBookingApi(
        @Body request: PanditjiBookingRequest
    ): PanditjiBookingResponse

    @GET("services")
    suspend fun getPanditjiServices(
        @Query("panditId") panditId: String
    ): GetServicesResponse

    @GET("getBookings")
    suspend fun getBookings(
        @Query("type") type: String? = null,
    ): GetBookingResponse

    @POST("updateBookingStatus")
    suspend fun updateBookingStatus(
        @Header("auth-token") token: String? = null,
        @Body userBookingRequest: UpdateBookingStatusRequest,
    ): GetBookingResponse

    @POST("createBookingPayment")
    suspend fun createBookingPayment(
        @Body request: BookingPaymentRequest,
    ): BookingPaymentResponse

    @GET("getAllLanguages")
    suspend fun getAllLanguages(): GetLanguagesResponse

    @POST("login-mobile")
    suspend fun loginWithMobile(
        @Body model: LoginWithMobileRequest?
    ): GetLoginResponse

    @POST("verify-mobile-otp")
    suspend fun verifyOtp(
        @Body model: RegistrationModel?
    ): GetSignUpResponse


    @POST("resend-mobile-otp")
    suspend fun reSendOtp(
        @Body model: SendOtpModel?
    ): ApiResponse
}
