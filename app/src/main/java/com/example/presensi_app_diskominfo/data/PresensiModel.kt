package com.example.presensi_app_diskominfo.data

data class PresensiModel(
    val date: String,
    val check_in: String?,
    val check_out: String?,
    val status: String,
)

data class CheckRequest(
    val code: String
)

data class PresensiResponse(
    val status: Boolean,
    val message: String,
    val data: List<PresensiModel>
)

data class CheckInModel(
    val date: String,
    val check_in: String?,
    val status: String
)

data class CheckInResponse(
    val status: Boolean,
    val message: String,
    val data: CheckInModel?
)

data class CheckOutModel(
    val date: String,
    val check_out: String?,
    val status: String
)

data class CheckOutResponse(
    val status: Boolean,
    val message: String,
    val data: CheckOutModel?
)