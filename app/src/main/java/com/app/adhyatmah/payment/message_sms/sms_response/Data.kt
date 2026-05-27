package com.app.adhyatmah.payment.message_sms.sms_response

data class Data(
    val batch: String,
    val category: String,
    val delivery: Boolean,
    val destinations: List<Destination>
)