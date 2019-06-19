package com.antonio.samir.meteoritelandingsspots.ui.widget

import android.annotation.TargetApi
import android.content.Context
import android.os.Binder
import android.os.Build
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.repository.local.MeteoriteDaoFactory
import org.apache.commons.lang3.StringUtils

class MeteoriteRemoteViewsFactory(private val mPackageName: String, private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {
    private var mMeteorites: List<Meteorite>? = null

    override fun onCreate() {

    }

    override fun onDataSetChanged() {

        val identityToken = Binder.clearCallingIdentity()

        mMeteorites = MeteoriteDaoFactory.getMeteoriteDao(mContext).meteoriteOrdenedSync

        Binder.restoreCallingIdentity(identityToken)
    }

    override fun onDestroy() {
        if (mMeteorites != null) {
            mMeteorites = null
        }
    }

    override fun getCount(): Int {
        return if (mMeteorites == null) 0 else mMeteorites!!.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(mPackageName, R.layout.meteorite_widget_item)

        val meteorite = mMeteorites!![position]

        val meteoriteName = meteorite.name
        val year = meteorite.yearString

        val idString = meteorite.id.toString()

        views.setTextViewText(R.id.title, meteoriteName)
        views.setTextViewText(R.id.year, year)
        setLocationText(meteorite.address, views)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            setRemoteContentDescription(views, meteoriteName)
        }

//        val intent = Intent(mContext, MeteoriteDetailActivity::class.java)
//        intent.putExtra(ITEM_SELECTED, idString)
//
//        views.setOnClickFillInIntent(R.id.widget_list_item, intent)

        return views
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(mPackageName, R.layout.meteorite_widget_item)
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return mMeteorites!![position].id.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    fun setLocationText(address: String?, views: RemoteViews) {
        val text: String?
        if (StringUtils.isNotEmpty(address)) {
            text = address
        } else {
            text = ""
        }
        views.setTextViewText(R.id.location, text)

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private fun setRemoteContentDescription(views: RemoteViews, description: String?) {
        views.setContentDescription(R.id.widget_list_item, description)
    }
}