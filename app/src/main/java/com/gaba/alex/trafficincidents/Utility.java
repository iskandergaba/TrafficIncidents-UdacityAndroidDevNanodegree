package com.gaba.alex.trafficincidents;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.gaba.alex.trafficincidents.Data.IncidentsColumns;
import com.gaba.alex.trafficincidents.Data.IncidentsProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Utility {

    public static void updateDatabase(Context context, JSONArray incidents, int statusCode) throws JSONException, RemoteException, OperationApplicationException {

        if (statusCode == 200) {
            context.getContentResolver().delete(IncidentsProvider.Incidents.CONTENT_URI, null, null);
            ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
            for (int i = 0; i < incidents.length(); i++) {
                JSONObject incident = incidents.getJSONObject(i);
                batchOperations.add(buildBatchOperation(incident));
            }
            context.getContentResolver().applyBatch(IncidentsProvider.AUTHORITY, batchOperations);
        }


    }

    public static void pushNotification(Context context, double lat, double lng, double range, int severity) {
        final int mNotificationId = 1;
        if (Utility.isAppOnForeground(context) && severity != 0) {
            String selection = "ABS(" + IncidentsColumns.LAT + " - " + lat + ") <= " + range +
                    " AND ABS(" + IncidentsColumns.LNG + " - " + lng + ") <= " + range +
                    " AND " + IncidentsColumns.SEVERITY + " >= " + severity;
            Log.v("fuck", selection);
            Uri uri = IncidentsProvider.Incidents.CONTENT_URI;
            Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("My notification")
                                .setContentText("Hello World!");
                if (BuildConfig.FREE_VERSION) {
                    Intent mainIntent = new Intent(context, MainActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(mainIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(mNotificationId, mBuilder.build());
                    cursor.close();
                }
            }
        }
    }

    private static boolean isAppOnForeground(Context context){

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

    private static ContentProviderOperation buildBatchOperation(JSONObject incident) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(IncidentsProvider.Incidents.CONTENT_URI);
        try {
            double lat = incident.getJSONObject("point").getJSONArray("coordinates").getDouble(0);
            double lng = incident.getJSONObject("point").getJSONArray("coordinates").getDouble(1);
            int type = incident.getInt("type");
            int severity = incident.getInt("severity");
            String id = incident.getString("incidentId");
            String description = incident.getString("description");
            String roadClosed = incident.getString("roadClosed");
            String startDateMillis = incident.getString("start");
            String endDateMillis = incident.getString("end");
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
