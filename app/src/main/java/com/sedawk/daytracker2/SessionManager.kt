package com.sedawk.daytracker2

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SessionManager @Inject constructor (@ApplicationContext context: Context) {
    private val prefs: SharedPreferences;
    private val editor: SharedPreferences.Editor;

    init {
        prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        println("Session Manager Initialized")
    }

    var name: String?
        get() = prefs.getString(KEY_CHALLENGE_NAME, null)
        set(challengeName) {
            editor.putString(KEY_CHALLENGE_NAME, challengeName)
            editor.apply()
        }

    var timestamp: Long
        get() = prefs.getLong(KEY_START_TIME, -1L)
        set(timestamp) {
            editor.putLong(KEY_START_TIME, timestamp)
            editor.apply()
        }

    companion object {
        private const val SHARED_PREF_NAME = "day_tracker";
        private const val KEY_CHALLENGE_NAME = "challenge_name";
        private const val KEY_START_TIME = "start_time";
    }
}