package com.app.adhyatmah.domain.model.bag_response.apply_coupons.get_all_apply_coupons

data class Coupon(
    val code: String,
    val ends_at: String,
    val price_rule_id: Long,
    val starts_at: String,
    val title: String,
    val usage_limit: Any,
    val value: String,
    val value_type: String
)