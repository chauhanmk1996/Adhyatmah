package com.app.adhyatmah.payment.message_sms.sms_response

data class Payload(
    val `data`: Data,
    val handshake: Handshake
)