package com.app.adhyatmah.domain.model.profile.edit_profile.edit_profile_request

data class EditProfileRequest(
    var accessToken: String?="",
    var email: String?="",
    var firstName: String?="",
    var image: String?="",
    var lastName: String?="",
    var phone: String?=""
)