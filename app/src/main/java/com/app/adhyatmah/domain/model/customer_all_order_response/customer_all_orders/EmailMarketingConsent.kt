package com.app.adhyatmah.domain.model.customer_all_order_response.customer_all_orders

data class EmailMarketingConsent(
    val consent_updated_at: String,
    val opt_in_level: String,
    val state: String
)