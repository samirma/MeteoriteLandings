package com.antonio.samir.meteoritelandingsspots.ui.widget

import android.annotation.TargetApi
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.TaskStackBuilder
import android.widget.RemoteViews

import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.ui.activity.MeteoriteDetailActivity

/**
 * Implementation of App Widget functionality.
 */
class MeteoritesAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.meteorites_app_widget)

            // Set up the collection
            setRemoteAdapter(context, views)

            val clickIntentTemplate = Intent(context, MeteoriteDetailActivity::class.java)
            val clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate)

            views.setEmptyView(R.id.widget_list, R.id.widget_empty)

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (ACTION_DATA_UPDATED == intent.action) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(context, javaClass))
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list)
        }
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private fun setRemoteAdapter(context: Context, views: RemoteViews) {
        views.setRemoteAdapter(R.id.widget_list,
                Intent(context, DetailWidgetRemoteViewsService::class.java))
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    private fun setRemoteAdapterV11(context: Context, views: RemoteViews) {
        views.setRemoteAdapter(0, R.id.widget_list,
                Intent(context, DetailWidgetRemoteViewsService::class.java))
    }

    companion object {
        val ACTION_DATA_UPDATED = "com.sam_chordas.android.stockhawk.widget.ACTION_DATA_UPDATED"
    }
}

