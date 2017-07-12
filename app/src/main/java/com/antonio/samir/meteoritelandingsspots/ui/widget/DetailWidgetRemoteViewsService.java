package com.antonio.samir.meteoritelandingsspots.ui.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViewsService;

/**
 * RemoteViewsService controlling the cursor being shown in the scrollable meteorite widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new MeteoriteRemoteViewsFactory(getPackageName());
    }

}
