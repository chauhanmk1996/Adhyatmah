package com.app.adhyatmah.domain.model.create_booking

data class PanditjiBookingRequest(
    var address: String?=null,
    var dateTime: String?=null,
    var duration: String?=null,
    var `package`: String?=null,
    var paymentAmount: String?=null,
    var gst: String?=null,
    var advance: String?=null,
    var poojaType: String?=null,
    var pujaDescription: String?=null,
    var pujaSamagri: List<String>?=null,
    var serviceId: String?=null,
    var vendorId: String?=null,
    var firstName: String?=null,
    var lastName: String?=null,
    var image: String?=null,
    var bookingId: String?=null,
    var language: List<String>?=null,
    var about: String?=null,
)