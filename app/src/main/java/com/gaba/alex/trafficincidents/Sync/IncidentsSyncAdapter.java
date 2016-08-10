package com.gaba.alex.trafficincidents.Sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

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

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class IncidentsSyncAdapter extends AbstractThreadedSyncAdapter {
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    public static final String BING_BASE_URL = "http://dev.virtualearth.net/REST/v1/Traffic/Incidents/";
    public static final String BING_API_KEY = "AusV2rdtPYqC440CZ4DV4GPUWv7tP8CSDdvATkk-bpChyUEw440vsCiOAkBj1Do0";
    public static final String BING_JSON_RESOURCE_SETS_KEY = "resourceSets";
    public static final String BING_JSON_RESULTS_KEY = "resources";

    /**
     * Set up the sync adapter
     */
    public IncidentsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        String prefLat = "lat";
        double lat = extras.getDouble(prefLat);
        String prefLng = "lng";
        double lng = extras.getDouble(prefLng);
        String prefSeverity = "prefNotifications";
        int severity = extras.getInt(prefSeverity);
        String prefRange = "prefSearchRange";
        double range = extras.getDouble(prefRange);


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
                incidentsJSON = new JSONObject(incidents).getJSONArray(BING_JSON_RESOURCE_SETS_KEY).getJSONObject(0).getJSONArray(BING_JSON_RESULTS_KEY);
                int statusCode = new JSONObject(incidents).getInt("statusCode");
                Utility.updateDatabase(getContext(), incidentsJSON, statusCode);
                Utility.pushNotification(getContext(), lat, lng, range, severity);

            } catch (JSONException | NullPointerException |OperationApplicationException | RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}