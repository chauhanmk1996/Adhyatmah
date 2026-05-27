package com.app.adhyatmah.domain.model.youtube_response

data class YoutubeResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)