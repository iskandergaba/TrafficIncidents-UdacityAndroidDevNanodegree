package com.gaba.alex.trafficincidents;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import com.gaba.alex.trafficincidents.Data.IncidentsColumns;
import com.gaba.alex.trafficincidents.Data.IncidentsProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
