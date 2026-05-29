package com.app.adhyatmah.domain.model.get_services

import com.app.adhyatmah.domain.model.Image

data class GetServicesResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: GetServicesPayload?,
    val status: Int,
)

data class GetServicesPayload(
    val services: ArrayList<Puja>?,
    val panditId: String?,
    val totalServices: Int?,
)

data class Puja(
    val id: String,
    val views: Int?,
    val image: String?,
    val poojaType: String?,
    val duration: String?,
    val price: Double?,
    val originalPrice: Double?,

    val description: String?,
    val gst: String?,
    val advance: String?,
    val vendor: Vendor?,
)

data class Vendor(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phone: String?,
    val role: String?,
)

data class GetPujaKitResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: GetPujaKitPayload?,
    val status: Int,
)

data class GetPujaKitPayload(
    val pujaKit: ArrayList<PujaKit>?,
    val instantKit: ArrayList<PujaKit>?,
)

data class PujaKit(
    val id: String,
    val image: String?,
    val name: String?,
    val slug: String?,
    val price: Double?,
    val originalPrice: Double?,
    var quantity: Int? = 0,
)