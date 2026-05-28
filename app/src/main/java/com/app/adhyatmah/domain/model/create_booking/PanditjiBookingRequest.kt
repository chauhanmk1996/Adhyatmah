package com.app.adhyatmah.domain.model.create_booking

data class PanditjiBookingRequest(
    var image: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var about: String? = null,
    var dateTime: String? = null,
    var duration: String? = null,
    var `package`: String? = null,
    var paymentAmount: String? = null,
    var gst: String? = null,
    var advance: String? = null,
    var poojaType: String? = null,
    var pujaDescription: String? = null,
    var pujaSamagri: List<String>? = null,
    var serviceId: String? = null,
    var vendorId: String? = null,
    var bookingId: String? = null,
    var address: String? = null,
    var language: List<String>? = null,
)

data class PanditJiDetails(
    var id: String? = null,
    var image: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var city: String? = null,
    var experience: String? = null,
    var about: String? = null,
    var seoContent: SeoContent? = null,
    var gotra: String? = null,
    var verified: Boolean? = null,
    var trusted: Boolean? = null,
)

data class SeoContent(
    val h1: String? = null,
    val intro: HeadingContent? = null,
    val about: HeadingContent? = null,
    val details: Details? = null,
    val faqs: ArrayList<Faq>? = null,
)

data class HeadingContent(
    val heading: String? = null,
    val content: Any,
)

data class Details(
    val experience: String? = null,
    val specialization: String? = null,
    val services: String? = null,
    val availability: String? = null,
)

data class Faq(
    val question: String? = null,
    val answer: String? = null,
    var isOpen: Boolean? = false,
)