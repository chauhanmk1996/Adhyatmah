package com.app.adhyatmah.domain.model.pandit_list.get_pandit_list

data class GetPanditResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)