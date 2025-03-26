package com.example.presensi_app_diskominfo.api

import com.example.presensi_app_diskominfo.data.CheckInResponse
import com.example.presensi_app_diskominfo.data.CheckOutResponse
import com.example.presensi_app_diskominfo.data.CheckRequest
import com.example.presensi_app_diskominfo.data.HistoryStudentResponse
import com.example.presensi_app_diskominfo.data.LoginRequest
import com.example.presensi_app_diskominfo.data.LoginResponse
import com.example.presensi_app_diskominfo.data.LogoutResponse
import com.example.presensi_app_diskominfo.data.PasswordRequest
import com.example.presensi_app_diskominfo.data.PresensiResponse
import com.example.presensi_app_diskominfo.data.StudentResponse
import com.example.presensi_app_diskominfo.data.UpdatedPasswordResponse
import com.example.presensi_app_diskominfo.data.UploadPhotoResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface AuthApiService {
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("logout")
    fun logout(@Header("Authorization") token: String): Call<LogoutResponse>

    @Multipart
    @POST("user/photo")
    suspend fun uploadPhoto(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part
    ): Response<UploadPhotoResponse>

    @GET("user/history")
    suspend fun getHistory(@Header("Authorization") token: String): Response<PresensiResponse>

    @POST("user/update/pass")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body request: PasswordRequest
    ): Response<UpdatedPasswordResponse>

    @POST("presensi/checkin")
    suspend fun checkIn(
        @Header("Authorization") token: String,
        @Body request: CheckRequest
    ): Response<CheckInResponse>

    @POST("presensi/checkout")
    suspend fun checkOut(
        @Header("Authorization") token: String,
        @Body request: CheckRequest
    ): Response<CheckOutResponse>

    @GET("mentor/students")
    suspend fun getStudentIntern(@Header("Authorization") token: String): Response<StudentResponse>

    @GET("mentor/students/history/{id}")
    suspend fun getStudentHistory(
        @Header("Authorization") authHeader: String,
        @Path("id") studentId: Int
    ): Response<HistoryStudentResponse>

}