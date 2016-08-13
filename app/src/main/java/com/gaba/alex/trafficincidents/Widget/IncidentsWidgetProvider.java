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
        Intent adapter = new Intent(context, WidgetService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        rv.setRemoteAdapter(R.id.list_view_widget, adapter);
        rv.setEmptyView(R.id.list_view_widget, R.id.empty_view_widget);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,
                R.id.list_view_widget);
    }
}