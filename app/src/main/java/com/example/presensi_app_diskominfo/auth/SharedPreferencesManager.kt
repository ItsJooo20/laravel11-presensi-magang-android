package com.example.presensi_app_diskominfo.auth

import android.content.Context
import android.content.SharedPreferences


class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_AUTH_TOKEN, token)
        editor.apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveUserData(name: String, email: String, role: String, photo: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER_NAME, name)
        editor.putString(KEY_USER_EMAIL, email)
        editor.putString(KEY_USER_ROLE, role)
        editor.putString(KEY_USER_PHOTO, photo)
        editor.apply()
    }

    fun saveMentorData(mentor_name: String, start_work_hour: String, finish_work_hour: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_MENTOR_NAME, mentor_name)
        editor.putString(KEY_START_TIME, start_work_hour)
        editor.putString(KEY_FINISH_TIME, finish_work_hour)
        editor.apply()
    }

    fun getMentorName(): String {
        return sharedPreferences.getString(KEY_MENTOR_NAME, "") ?: ""
    }

    fun getStartTime(): String {
        return sharedPreferences.getString(KEY_START_TIME, "") ?: ""
    }

    fun getFinishTime(): String {
        return sharedPreferences.getString(KEY_FINISH_TIME, "") ?: ""
    }

    fun getUserName(): String {
        return sharedPreferences.getString(KEY_USER_NAME, "") ?: ""
    }

    fun getUserPhoto(): String {
        return sharedPreferences.getString(KEY_USER_PHOTO, "") ?: ""
    }

    fun getUserEmail(): String {
        return sharedPreferences.getString(KEY_USER_EMAIL, "") ?: ""
    }

    fun getUserRole(): String {
        return sharedPreferences.getString(KEY_USER_ROLE, "") ?: ""
    }

    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }

    fun clearUserData() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        private const val PREF_NAME = "AUTH_PREFS"
        private const val KEY_AUTH_TOKEN = "AUTH_TOKEN"
        private const val KEY_USER_NAME = "USER_NAME"
        private const val KEY_USER_EMAIL = "USER_EMAIL"
        private const val KEY_USER_ROLE = "USER_ROLE"
        private const val KEY_USER_PHOTO = "USER_PHOTO"
        private const val KEY_MENTOR_NAME = "MENTOR_NAME"
        private const val KEY_START_TIME = "START_TIME"
        private const val KEY_FINISH_TIME = "FINISH_TIME"

        @Volatile
        private var instance: SharedPreferencesManager? = null

        fun getInstance(context: Context): SharedPreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: SharedPreferencesManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}