package com.app.adhyatmah.payment.message_sms.sms_response

data class SMSResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)