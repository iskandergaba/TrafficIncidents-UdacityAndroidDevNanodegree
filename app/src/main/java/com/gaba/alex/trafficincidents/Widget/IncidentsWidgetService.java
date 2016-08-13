package com.gaba.alex.trafficincidents.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class IncidentsWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new IncidentsWidgetListProvider(getApplicationContext(), intent));
    }
}