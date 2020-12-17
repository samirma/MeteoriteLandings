package com.antonio.samir.meteoritelandingsspots.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.antonio.samir.meteoritelandingsspots.R

/**
 * Implementation of App Widget functionality.
 */
class MeteoritesAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Perform this loop procedure for each App Widget that belongs to this provider
//        for (appWidgetId in appWidgetIds) {
//            val views = RemoteViews(context.packageName, R.layout.meteorites_app_widget)
//
//            // Set up the collection
//            setRemoteAdapter(context, views)
//
//            val clickIntentTemplate = Intent(context, MeteoriteDetailActivity::class.java)
//            val clickPendingIntentTemplate = TaskStackBuilder.create(context)
//                    .addNextIntentWithParentStack(clickIntentTemplate)
//                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
//
//            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate)
//
//            views.setEmptyView(R.id.widget_list, R.id.widget_empty)
//
//            // Tell the AppWidgetManager to perform an update on the current app widget
//            appWidgetManager.updateAppWidget(appWidgetId, views)
//        }
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
    
    companion object {
        const val ACTION_DATA_UPDATED = "com.sam_chordas.android.stockhawk.widget.ACTION_DATA_UPDATED"
    }
}

