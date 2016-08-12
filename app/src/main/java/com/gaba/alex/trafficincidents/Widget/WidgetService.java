package com.gaba.alex.trafficincidents.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new WidgetListProvider(getApplicationContext(), intent));
    }
}