package com.app.adhyatmah.domain.model.get_services

data class GetServicesResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val panditId: String,
        val services: List<Service>,
        val totalServices: Int
    ) {
        data class Service(
            val description: String,
            val gst: String,
            val advance: String,
            val duration: String,
            val id: String,
            val poojaType: String,
            val price: Int,
            val vendor: Vendor
        ) {
            data class Vendor(
                val email: String,
                val firstName: String,
                val id: String,
                val lastName: String,
                val phone: String,
                val role: String
            )
        }
    }
}