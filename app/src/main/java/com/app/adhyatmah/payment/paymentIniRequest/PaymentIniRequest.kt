package com.app.adhyatmah.payment.paymentIniRequest

data class PaymentIniRequest(
    var accessToken: String?="",
    var addressId: String?="",
    var cartId: String?="",
    var currency: String?="",
    var email: String?=""
)