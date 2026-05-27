package com.app.adhyatmah.domain.model.profile.get_img_profile

data class GetProfileImgResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)