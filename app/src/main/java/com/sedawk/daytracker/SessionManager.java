package com.sedawk.daytracker;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    private static final String SHARED_PREF_NAME = "day_tracker";
    private static final String KEY_CHALLENGE_NAME = "challenge_name";
    private static final String KEY_START_TIME = "start_time";

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setName(String challengeName){
        editor.putString(KEY_CHALLENGE_NAME , challengeName);
        editor.apply();
    }

    public String getName(){
        return sharedPreferences.getString(KEY_CHALLENGE_NAME , null);
    }

    public void setTimestamp(long timestamp){
        editor.putLong(KEY_START_TIME , timestamp);
        editor.apply();
    }

    public long getTimestamp(){
        return sharedPreferences.getLong(KEY_START_TIME , -1L);
    }
}
