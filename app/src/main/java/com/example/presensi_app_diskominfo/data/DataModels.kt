package com.example.presensi_app_diskominfo.data

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val photo: String?,
    val token: String
)

data class Mentor(
    val name: String,
    val start_work_hour: String?,
    val finish_work_hour: String?,
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val status: Boolean,
    val message: String,
    val data: LoginData?
)

data class LoginData(
    val user: User,
    val token: String,
    val mentor: Mentor
)

data class LogoutResponse(
    val status: Boolean,
    val message: String
)

data class UploadPhotoResponse(
    val status: Boolean,
    val message: String,
    val photo: String?
)

data class PasswordRequest(
    val password: String
)

data class UpdatedPasswordResponse(
    val status: Boolean,
    val message: String
)
