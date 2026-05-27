package com.app.adhyatmah.payment.payment_varify_response

data class Authorization(
    val account_name: Any,
    val authorization_code: String,
    val bank: String,
    val bin: String,
    val brand: String,
    val card_type: String,
    val channel: String,
    val country_code: String,
    val exp_month: String,
    val exp_year: String,
    val last4: String,
    val mobile_money_number: String,
    val receiver_bank: Any,
    val receiver_bank_account_number: Any,
    val reusable: Boolean,
    val signature: Any
)