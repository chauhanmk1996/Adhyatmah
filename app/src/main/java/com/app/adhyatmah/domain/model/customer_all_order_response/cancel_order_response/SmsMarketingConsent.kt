package com.app.adhyatmah.domain.model.customer_all_order_response.cancel_order_response

data class SmsMarketingConsent(
    val consent_collected_from: String,
    val consent_updated_at: Any,
    val opt_in_level: String,
    val state: String
)