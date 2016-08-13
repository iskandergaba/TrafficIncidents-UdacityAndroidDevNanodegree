package com.gaba.alex.trafficincidents.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.gaba.alex.trafficincidents.Data.IncidentsColumns;
import com.gaba.alex.trafficincidents.Data.IncidentsProvider;
import com.gaba.alex.trafficincidents.Data.SettingsColumns;
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
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_view_item);
        if (mCursor.moveToPosition(position)) {
            Intent intent = Utility.buildShowOnMapIntent(mCursor.getDouble(mCursor.getColumnIndex(IncidentsColumns.LAT)),
                    mCursor.getDouble(mCursor.getColumnIndex(IncidentsColumns.LNG)));
            rv.setTextViewText(R.id.incident_type_widget,
                    Utility.getIncidentType(mCursor.getInt(mCursor.getColumnIndex(IncidentsColumns.TYPE))));
            rv.setTextViewText(R.id.incident_description_widget,
                    mCursor.getString(mCursor.getColumnIndex(IncidentsColumns.DESCRIPTION)));
            rv.setOnClickFillInIntent(R.id.show_on_map_button_widget, intent);
            rv.setOnClickFillInIntent(R.id.widget_row, intent);
        }
        return rv;
    }

    @Override
    public void onCreate() {
        Uri uri = IncidentsProvider.Settings.CONTENT_URI;
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        String selection = null;
        if (cursor != null && cursor.moveToFirst()) {
            double lat = cursor.getDouble(cursor.getColumnIndex(SettingsColumns.LAT));
            double lng = cursor.getDouble(cursor.getColumnIndex(SettingsColumns.LNG));
            double range = cursor.getDouble(cursor.getColumnIndex(SettingsColumns.RANGE));
            selection = "ABS(" + IncidentsColumns.LAT + " - " +  lat + ") <= " + range +
                    " AND ABS(" + IncidentsColumns.LNG + " - " +  lng + ") <= " + range;
            cursor.close();
        }
        mCursor = mContext.getContentResolver().query(IncidentsProvider.Incidents.CONTENT_URI,
                new String[]{IncidentsColumns.LAT, IncidentsColumns.LNG, IncidentsColumns.TYPE,
                        IncidentsColumns.DESCRIPTION}, selection, null,  IncidentsColumns.SEVERITY + " DESC");
    }

    @Override
    public void onDataSetChanged() {
        Uri uri = IncidentsProvider.Settings.CONTENT_URI;
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        String selection = null;
        if (cursor != null && cursor.moveToFirst()) {
            double lat = cursor.getDouble(cursor.getColumnIndex(SettingsColumns.LAT));
            double lng = cursor.getDouble(cursor.getColumnIndex(SettingsColumns.LNG));
            double range = cursor.getDouble(cursor.getColumnIndex(SettingsColumns.RANGE));
            selection = "ABS(" + IncidentsColumns.LAT + " - " +  lat + ") <= " + range +
                    " AND ABS(" + IncidentsColumns.LNG + " - " +  lng + ") <= " + range;
            cursor.close();
        }
        mCursor = mContext.getContentResolver().query(IncidentsProvider.Incidents.CONTENT_URI,
                new String[]{IncidentsColumns.LAT, IncidentsColumns.LNG, IncidentsColumns.TYPE,
                        IncidentsColumns.DESCRIPTION}, selection, null,  IncidentsColumns.SEVERITY + " DESC");
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