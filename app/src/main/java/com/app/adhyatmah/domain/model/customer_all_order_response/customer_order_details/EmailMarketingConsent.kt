package com.app.adhyatmah.domain.model.customer_all_order_response.customer_order_details

data class EmailMarketingConsent(
    val consent_updated_at: String,
    val opt_in_level: String,
    val state: String
)