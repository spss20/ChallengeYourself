package com.sedawk.daytracker

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences;
    private val editor: SharedPreferences.Editor;

    init {
        prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
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