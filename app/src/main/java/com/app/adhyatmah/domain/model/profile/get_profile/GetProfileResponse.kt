package com.app.adhyatmah.domain.model.profile.get_profile

data class GetProfileResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload?=null,
    val status: Int
)