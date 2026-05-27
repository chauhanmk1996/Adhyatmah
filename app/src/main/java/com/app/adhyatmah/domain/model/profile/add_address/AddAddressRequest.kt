package com.app.adhyatmah.domain.model.profile.add_address

data class AddAddressRequest(
    var accessToken: String?="",
    var addressId: String?="",
    var address: Address?=null)

{

    data class Address(
        var firstName: String?="",
        var lastName: String?="",
        var address1: String?="",
        var address2: String?="",
        var city: String?="",
        var province: String?="",
        var country: String?="",
        var zip: String?="",
        var phone: String?=""
    )

}




