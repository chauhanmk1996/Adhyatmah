package com.app.adhyatmah.domain.model

import com.app.adhyatmah.domain.model.pandit_list.get_pandit_list.Payload

data class TrendingSectionResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val status: Int,
    val payload: TrendingSectionPayload?,
)

data class TrendingSectionPayload(
    val collections: ArrayList<TrendingSection>?,
)

data class TrendingSection(
    val id: String,
    val handle: String?,
    val title: String?,
    val image: Image?,
)

data class Image(
    val url: String?,
)