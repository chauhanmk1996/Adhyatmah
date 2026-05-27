package com.app.adhyatmah.domain.model.youtube_url_response

data class YoutubeUrlResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)