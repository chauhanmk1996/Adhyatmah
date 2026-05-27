package com.app.adhyatmah.payment.message_sms.sms_response

data class Destination(
    val id: String,
    val status: Status,
    val to: String
)