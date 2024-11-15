package com.hash.medmarket.utils

import android.content.Context
import com.google.gson.Gson
import com.hash.medmarket.database.model.Users

object UserAuthManager {

    private const val PREFS_NAME = "UserPrefs"

    fun saveUserData(context: Context, user: Users) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("Data", Gson().toJson(user))
        editor.apply()
    }

    fun getUserData(context: Context): Users? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return Gson().fromJson(sharedPreferences.getString("Data", ""), Users::class.java)
    }

    fun clearUserData(context: Context){
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

}
