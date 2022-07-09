package com.antonio.samir.meteoritelandingsspots.common.ui.widget

import android.annotation.TargetApi
import android.os.Binder
import android.os.Build
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.features.yearString

class MeteoriteRemoteViewsFactory(
    private val mPackageName: String
) : RemoteViewsService.RemoteViewsFactory {

    private var meteorites: List<Meteorite>? = null

    override fun onCreate() {

    }

    override fun onDataSetChanged() {

        val identityToken = Binder.clearCallingIdentity()

//        meteorites = meteoriteRepository.meteoriteOrdered(null, null).create()

        Binder.restoreCallingIdentity(identityToken)
    }

    override fun onDestroy() {
        if (meteorites != null) {
            meteorites = null
        }
    }

    override fun getCount(): Int {
        return if (meteorites == null) 0 else meteorites!!.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(mPackageName, R.layout.meteorite_widget_item)

        val meteorite = meteorites!![position]

        val meteoriteName = meteorite.name
        val year = meteorite.yearString

//        views.setTextViewText(R.id.title, meteoriteName)
//        views.setTextViewText(R.id.year, year)
//        setLocationText(meteorite.address, views)

        setRemoteContentDescription(views, meteoriteName)

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
        return meteorites!![position].id.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

//    private fun setLocationText(address: String?, views: RemoteViews) {
//        val text: String?
//        text = if (StringUtils.isNotEmpty(address)) {
//            address
//        } else {
//            ""
//        }
////        views.setTextViewText(R.id.location, text)
//
//    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private fun setRemoteContentDescription(views: RemoteViews, description: String?) {
        views.setContentDescription(R.id.widget_list_item, description)
    }
}