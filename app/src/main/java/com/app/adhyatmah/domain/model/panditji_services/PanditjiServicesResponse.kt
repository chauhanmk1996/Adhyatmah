package com.app.panditji.data.model.panditji_services

data class PanditjiServicesResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val services: List<Service>
    ) {
        data class Service(
            val description: String,
            val duration: String,
            val id: String,
            val poojaType: String,
            val price: Int
        )
    }
}