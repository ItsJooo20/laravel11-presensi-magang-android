package com.example.presensi_app_diskominfo.data

data class Student(
    val id: Int,
    val name: String,
    val email: String?,
    val id_institution: Int
)

data class StudentSchedule(
    val start_working_time: String,
    val finish_working_time: String,
    val student: Student
)

data class StudentResponse(
    val status: Boolean,
    val message: String,
    val data: List<StudentSchedule>
)

data class HistoryStudentResponse(
    val status: Boolean,
    val message: String,
    val data: List<HistoryStudentModel>
)

data class HistoryStudentModel(
    val date: String,
    val check_in: String?,
    val check_out: String?,
    val status: String
)
