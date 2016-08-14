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

package com.gaba.alex.trafficincidents;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.gaba.alex.trafficincidents.Data.IncidentsColumns;
import com.gaba.alex.trafficincidents.Data.IncidentsProvider;
import com.gaba.alex.trafficincidents.Data.SettingsColumns;
import com.gaba.alex.trafficincidents.Widget.IncidentsWidgetProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Utility {

    public static final String BING_JSON_POINT_KEY = "point";
    public static final String BING_JSON_COORDINATES_KEY = "coordinates";
    public static final String BING_JSON_TYPE_KEY = "type";
    public static final String BING_JSON_SEVERITY_KEY = "severity";
    public static final String BING_JSON_INCIDENT_ID_KEY = "incidentId";
    public static final String BING_JSON_DESCRIPTION_KEY = "description";
    public static final String BING_JSON_ROAD_CLOSED_KEY = "roadClosed";
    public static final String BING_JSON_START_DATE_KEY = "start";
    public static final String BING_JSON_END_DATE_KEY = "end";

    public static void updateDatabase(Context context, JSONArray incidents, double lat, double lng, int statusCode) throws JSONException, RemoteException, OperationApplicationException {

        if (statusCode == 200 && (lat != 0 || lng != 0)) {
            context.getContentResolver().delete(IncidentsProvider.Incidents.CONTENT_URI, null, null);
            ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
            for (int i = 0; i < incidents.length(); i++) {
                JSONObject incident = incidents.getJSONObject(i);
                batchOperations.add(buildBatchOperation(incident));
            }
            context.getContentResolver().applyBatch(IncidentsProvider.AUTHORITY, batchOperations);
        }
    }

    public static void updateSettings(Context context) throws RemoteException, OperationApplicationException {
        context.getContentResolver().delete(IncidentsProvider.Settings.CONTENT_URI, null, null);
        context.getContentResolver().insert(IncidentsProvider.Settings.CONTENT_URI, buildSettingsValues(context));
    }

    public static void updateWidget(Context context) {
        Intent intent = new Intent(context, IncidentsWidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        ComponentName name = new ComponentName(context, IncidentsWidgetProvider.class);
        int [] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(name);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    public static String getIncidentType(Context context, int type) {
        String[] typeCodes = context.getResources().getStringArray(R.array.incidentTypeCodes);
        return typeCodes[type - 1];
    }

    public static String getIncidentSeverity(Context context, int severity) {
        final String[] severityCodes = context.getResources().getStringArray(R.array.incidentSeverityCodes);
        return severityCodes[severity - 1];
    }

    public static int getIncidentColor(int type) {
        int[] typeColors = {R.color.low_impact, R.color.minor, R.color.moderate, R.color.serious};
        return typeColors[type - 1];
    }

    public static Intent buildShowOnMapIntent(double lat, double lng) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(String.format("geo:%s,%s", lat, lng)));
        return intent;
    }

    public static void pushNotification(Context context, double lat, double lng, double range, int severity) {
        final int mNotificationId = 1;
        if (!isAppOnForeground(context) && severity != 0) {
            String selection = "ABS(" + IncidentsColumns.LAT + " - " + lat + ") <= " + range +
                    " AND ABS(" + IncidentsColumns.LNG + " - " + lng + ") <= " + range +
                    " AND " + IncidentsColumns.SEVERITY + " >= " + severity;
            Uri uri = IncidentsProvider.Incidents.CONTENT_URI;
            Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(android.R.drawable.stat_notify_sync)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.playstore_icon))
                                .setContentTitle(context.getString(R.string.app_name))
                                .setContentText(context.getString(R.string.notification_content))
                                .setAutoCancel(true);
                Intent mainIntent = new Intent(context, MainActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(mainIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(mNotificationId, mBuilder.build());
                cursor.close();
            }
        }
    }

    private static boolean isAppOnForeground(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses != null) {
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static ContentValues buildSettingsValues(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        ContentValues values = new ContentValues();
        double lat = Double.parseDouble(preferences.getString("lat", "0"));
        double lng = Double.parseDouble(preferences.getString("lng", "0"));
        double range = Double.parseDouble(preferences.getString(context.getString(R.string.pref_search_range_key), "0.05"));
        int severity = Integer.parseInt(preferences.getString(context.getString(R.string.pref_notifications_key), "4"));
        int autoRefresh= Integer.parseInt(preferences.getString(context.getString(R.string.pref_auto_refresh_key), "6"));
        values.put(SettingsColumns.LAT, lat);
        values.put(SettingsColumns.LNG, lng);
        values.put(SettingsColumns.RANGE, range);
        values.put(SettingsColumns.SEVERITY, severity);
        values.put(SettingsColumns.AUTO_REFRESH, autoRefresh);
        return values;
    }

    private static ContentProviderOperation buildBatchOperation(JSONObject incident) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(IncidentsProvider.Incidents.CONTENT_URI);
        try {
            double lat = incident.getJSONObject(BING_JSON_POINT_KEY).getJSONArray(BING_JSON_COORDINATES_KEY).getDouble(0);
            double lng = incident.getJSONObject(BING_JSON_POINT_KEY).getJSONArray(BING_JSON_COORDINATES_KEY).getDouble(1);
            int type = incident.getInt(BING_JSON_TYPE_KEY);
            int severity = incident.getInt(BING_JSON_SEVERITY_KEY);
            String id = incident.getString(BING_JSON_INCIDENT_ID_KEY);
            String description = incident.getString(BING_JSON_DESCRIPTION_KEY);
            String roadClosed = incident.getString(BING_JSON_ROAD_CLOSED_KEY);
            String startDateMillis = incident.getString(BING_JSON_START_DATE_KEY);
            String endDateMillis = incident.getString(BING_JSON_END_DATE_KEY);
            startDateMillis = startDateMillis.substring(6, startDateMillis.length() - 2);
            endDateMillis = endDateMillis.substring(6, endDateMillis.length() - 2);
            builder.withValue(IncidentsColumns.LAT, lat);
            builder.withValue(IncidentsColumns.LNG, lng);
            builder.withValue(IncidentsColumns.TYPE, type);
            builder.withValue(IncidentsColumns.SEVERITY, severity);
            builder.withValue(IncidentsColumns._ID, id);
            builder.withValue(IncidentsColumns.DESCRIPTION, description);
            builder.withValue(IncidentsColumns.ROAD_CLOSED, roadClosed);
            builder.withValue(IncidentsColumns.START_DATE, startDateMillis);
            builder.withValue(IncidentsColumns.END_DATE, endDateMillis);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.build();
    }
}