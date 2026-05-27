package com.app.adhyatmah.payment.message_sms.sms_request

data class SMSRequest(
    var destinations: List<String>?=null,
    var sender: String?="",
    var text: String?=""
)