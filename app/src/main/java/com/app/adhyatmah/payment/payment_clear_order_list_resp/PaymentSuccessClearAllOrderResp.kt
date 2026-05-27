package com.app.adhyatmah.payment.payment_clear_order_list_resp

data class PaymentSuccessClearAllOrderResp(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)