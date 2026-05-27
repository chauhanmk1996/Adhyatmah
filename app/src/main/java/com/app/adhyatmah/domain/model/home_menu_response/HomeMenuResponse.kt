package com.app.adhyatmah.domain.model.home_menu_response

data class HomeMenuResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)