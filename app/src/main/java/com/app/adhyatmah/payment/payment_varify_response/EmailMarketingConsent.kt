package com.app.adhyatmah.payment.payment_varify_response

data class EmailMarketingConsent(
    val consent_updated_at: String,
    val opt_in_level: String,
    val state: String
)