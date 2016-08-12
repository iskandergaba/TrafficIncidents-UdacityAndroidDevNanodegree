package com.gaba.alex.trafficincidents.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.gaba.alex.trafficincidents.Data.IncidentsColumns;
import com.gaba.alex.trafficincidents.Data.IncidentsProvider;
import com.gaba.alex.trafficincidents.R;
import com.gaba.alex.trafficincidents.Utility;

public class WidgetListProvider implements RemoteViewsService.RemoteViewsFactory {
    private Cursor mCursor;
    private Context mContext;
    int mWidgetId;

    public WidgetListProvider(Context context, Intent intent) {
        mContext = context;
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.list_view_item);
        if (mCursor.moveToPosition(position)) {
            Intent intent = Utility.buildShowOnMapIntent(mCursor.getDouble(mCursor.getColumnIndex(IncidentsColumns.LAT)),
                    mCursor.getDouble(mCursor.getColumnIndex(IncidentsColumns.LNG)));
            PendingIntent showOnMapPendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            rv.setTextViewText(R.id.incident_type_widget,
                    Utility.getIncidentType(mCursor.getInt(mCursor.getColumnIndex(IncidentsColumns.TYPE))));
            rv.setTextViewText(R.id.incident_description_widget,
                    mCursor.getString(mCursor.getColumnIndex(IncidentsColumns.DESCRIPTION)));
            rv.setOnClickPendingIntent(R.id.show_on_map_button_widget, showOnMapPendingIntent);

        }
        return rv;
    }

    @Override
    public void onCreate() {
        mCursor = mContext.getContentResolver().query(IncidentsProvider.Incidents.CONTENT_URI,
                new String[]{IncidentsColumns.LAT, IncidentsColumns.LNG, IncidentsColumns.TYPE,
                        IncidentsColumns.DESCRIPTION}, null, null, null);
    }

    @Override
    public void onDataSetChanged() {
        mCursor = mContext.getContentResolver().query(IncidentsProvider.Incidents.CONTENT_URI,
                new String[]{IncidentsColumns.LAT, IncidentsColumns.LNG, IncidentsColumns.TYPE,
                        IncidentsColumns.DESCRIPTION}, null, null, null);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}