package com.gaba.alex.trafficincidents.Sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.gaba.alex.trafficincidents.Data.IncidentsProvider;
import com.gaba.alex.trafficincidents.Data.SettingsColumns;
import com.gaba.alex.trafficincidents.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IncidentsSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String BING_BASE_URL = "http://dev.virtualearth.net/REST/v1/Traffic/Incidents/";
    public static final String BING_API_KEY = "AusV2rdtPYqC440CZ4DV4GPUWv7tP8CSDdvATkk-bpChyUEw440vsCiOAkBj1Do0";
    public static final String BING_JSON_RESOURCE_SETS_KEY = "resourceSets";
    public static final String BING_JSON_RESULTS_KEY = "resources";


    public IncidentsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        double lat = 0;
        double lng = 0;
        double range = 0.05;
        int severity = 4;

        Uri uri = IncidentsProvider.Settings.CONTENT_URI;
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            lat = cursor.getDouble(cursor.getColumnIndex(SettingsColumns.LAT));
            lng = cursor.getDouble(cursor.getColumnIndex(SettingsColumns.LNG));
            range = cursor.getDouble(cursor.getColumnIndex(SettingsColumns.RANGE));
            severity = cursor.getInt(cursor.getColumnIndex(SettingsColumns.SEVERITY));
            cursor.close();
        }

        String incidents = null;
        JSONArray incidentsJSON;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            String link = BING_BASE_URL + (lat - 0.1) + "," + (lng + 0.1) + "," + (lat + 0.1) + "," + (lng - 0.1)
                    + "?key=" + BING_API_KEY;
            URL url = new URL(link);
            Log.v("fuck", link);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            incidents = buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (urlConnection != null)
                urlConnection.disconnect();

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                incidentsJSON = new JSONObject(incidents)
                        .getJSONArray(BING_JSON_RESOURCE_SETS_KEY)
                        .getJSONObject(0)
                        .getJSONArray(BING_JSON_RESULTS_KEY);
                int statusCode = new JSONObject(incidents).getInt("statusCode");
                Utility.updateDatabase(getContext(), incidentsJSON, statusCode);
                Utility.pushNotification(getContext(), lat, lng, range, severity);

            } catch (JSONException | NullPointerException |OperationApplicationException | RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}