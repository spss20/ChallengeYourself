package com.sedawk.daytracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

public class MyWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "MyWidgetProvider";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(TAG, "Widget Provider enabled. Setting alarm");

        // Schedule the alarm to trigger at midnight each day
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC, getMidnightTimeInMillis(), AlarmManager.INTERVAL_DAY, getPendingIntent(context));
//        alarmManager.setRepeating(AlarmManager.RTC, getMinuteTimeInMillis(), 60 * 1000, getPendingIntent(context));

    }

    private PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, MyWidgetAlarmReceiver.class);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
    }
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d(TAG, "Widget Provider disabled. Turning off timer");
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getPendingIntent(context));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v("DayTracker" , "On Update Called");
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context , MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setTextViewText(R.id.widget_text, getTime(context));
            views.setOnClickPendingIntent(R.id.widget_layout , pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
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

    private long getMinuteTimeInMillis() {
        Calendar midnight = Calendar.getInstance();
        midnight.setTimeInMillis(System.currentTimeMillis());
        midnight.add(Calendar.MINUTE , 1);
        midnight.set(Calendar.SECOND, 0);
        return midnight.getTimeInMillis();
    }

    private long getMidnightTimeInMillis() {
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);
        midnight.add(Calendar.DAY_OF_MONTH, 1);
        return midnight.getTimeInMillis();
    }
}