package com.sedawk.daytracker;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyWidgetAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Update your widget here
        // Get the widget ids for all instances of your widget
        Log.v(TAG , "Alarm Receiver Called");
        ComponentName componentName = new ComponentName(context, MyWidgetProvider.class);
        int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(componentName);

        // Update the views for each instance of the widget
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setTextViewText(R.id.widget_text, getTime(context));
            // Update the views here
            AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views);
        }
    }

    private String getTime(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        if(sessionManager.getTimestamp() == -1){
            return "404";
        }
        long diff = (System.currentTimeMillis() - sessionManager.getTimestamp());
        long diffDays = (diff / (24 * 60 * 60 * 1000));
        return String.valueOf(diffDays + 1L);
    }


}