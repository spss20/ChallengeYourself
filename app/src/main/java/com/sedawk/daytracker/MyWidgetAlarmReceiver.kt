package com.sedawk.daytracker

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews

class MyWidgetAlarmReceiver: BroadcastReceiver() {

    companion object {
        private const val TAG = "AlarmReceiver";
    }

    override fun onReceive(context: Context, intent: Intent?) {
        // Update your widget here
        // Get the widget ids for all instances of your widget
        Log.v(TAG, "Alarm Receiver Called")
        val componentName = ComponentName(context, MyWidgetProvider::class.java)
        val appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(componentName)

        // Update the views for each instance of the widget
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setTextViewText(R.id.widget_text, getTime(context))
            // Update the views here
            AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views)
        }
    }

    private fun getTime(context: Context): String? {
        val sessionManager = SessionManager(context)
        if (sessionManager.timestamp == -1L) {
            return "404"
        }
        val diff = System.currentTimeMillis() - sessionManager.timestamp
        val diffDays = diff / (24 * 60 * 60 * 1000)
        return (diffDays + 1L).toString()
    }

}