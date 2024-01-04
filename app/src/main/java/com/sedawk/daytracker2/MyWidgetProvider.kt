package com.sedawk.daytracker2

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import java.util.Calendar


class MyWidgetProvider : AppWidgetProvider() {

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        Log.d(TAG, "Widget Provider enabled. Setting alarm")

        // Schedule the alarm to trigger at midnight each day
        val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC,
            getMidnightTimeInMillis(),
            AlarmManager.INTERVAL_DAY,
            getPendingIntent(context)
        )

    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        Log.d(TAG, "Widget Provider disabled. Turning off timer");
        val alarmManager = context!!
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(getPendingIntent(context))
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        Log.v(TAG , "On Update Called");
        for (appWidgetId in appWidgetIds!!) {
            val intent = Intent(context, MainActivity::class.java)

            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val views = RemoteViews(context!!.packageName, R.layout.widget_layout)
            views.setTextViewText(R.id.widget_text, getTime(context))
            views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent)
            appWidgetManager!!.updateAppWidget(appWidgetId, views)
        }

    }

    private fun getTime(context: Context): String {
        val sessionManager = SessionManager(context);
        if(sessionManager.timestamp == -1L){
            return "404";
        }
        val diff = System.currentTimeMillis() - sessionManager.timestamp;
        val diffDays = (diff / (24 * 60 * 60 * 1000));
        return (diffDays + 1L).toString();
    }

    private fun getPendingIntent(context: Context?): PendingIntent {
        val intent = Intent(context, MyWidgetAlarmReceiver::class.java);
        return PendingIntent.getBroadcast(context, 0 , intent , PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE )
    }

    private fun getMinuteTimeInMillis(): Long {
        val midnight = Calendar.getInstance()
        midnight.timeInMillis = System.currentTimeMillis()
        midnight.add(Calendar.MINUTE, 1)
        midnight[Calendar.SECOND] = 0
        return midnight.timeInMillis
    }

    fun getMidnightTimeInMillis(): Long{
        val midnight = Calendar.getInstance()
        midnight[Calendar.HOUR_OF_DAY] = 0
        midnight[Calendar.MINUTE] = 0
        midnight[Calendar.SECOND] = 0
        midnight[Calendar.MILLISECOND] = 0
        midnight.add(Calendar.DAY_OF_MONTH, 1)
        return midnight.timeInMillis
    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        // Call onUpdate to handle widget resizing
        onUpdate(context, appWidgetManager, intArrayOf(appWidgetId))
    }
    companion object {
        private const val TAG = "MyWidgetProvider"
    }
}