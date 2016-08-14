/*
Copyright 2016 Iskander Gaba

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.gaba.alex.trafficincidents.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.gaba.alex.trafficincidents.MainActivity;
import com.gaba.alex.trafficincidents.R;

public class IncidentsWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for (int i : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, i);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews rv = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 1, new Intent(context, MainActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent showOnMapPendingIntent = PendingIntent.getActivity(context, 0, new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.widget_name, mainPendingIntent);
        rv.setPendingIntentTemplate(R.id.list_view_widget, showOnMapPendingIntent);
        Intent adapter = new Intent(context, IncidentsWidgetService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        rv.setRemoteAdapter(R.id.list_view_widget, adapter);
        rv.setEmptyView(R.id.list_view_widget, R.id.empty_view_widget);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,
                R.id.list_view_widget);
    }
}