package com.app.adhyatmah.domain.model.customer_all_order_response.customer_order_details

data class Customer(
    val admin_graphql_api_id: String,
    val created_at: String,
    val currency: String,
    val default_address: DefaultAddress,
    val email_marketing_consent: EmailMarketingConsent,
    val id: Long,
    val multipass_identifier: Any,
    val note: Any,
    val sms_marketing_consent: SmsMarketingConsent,
    val state: String,
    val tags: String,
    val tax_exempt: Boolean,
    val tax_exemptions: List<Any>,
    val updated_at: String,
    val verified_email: Boolean
)