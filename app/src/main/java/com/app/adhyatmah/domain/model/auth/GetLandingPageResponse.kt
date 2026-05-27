package com.app.adhyatmah.domain.model.auth

data class GetLandingPageResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)

{
    data class Payload(
        val landingPages: List<LandingPage>
    )

    data class LandingPage(
        val handle: String,
        val id: String,
        val landing_page_image_url: String,
        val type: String,
        val title: String,
        val description: String
    )



}